#!/usr/bin/env bash

set -euo pipefail

operation="${CATALOG_OPERATION:?CATALOG_OPERATION is required}"
site_dir="${CATALOG_SITE_DIR:-}"
pr_number="${CATALOG_PR_NUMBER:-}"
source_sha="${CATALOG_SOURCE_SHA:-}"
output_dir="${CATALOG_OUTPUT_DIR:?CATALOG_OUTPUT_DIR is required}"
require_latest_develop="${CATALOG_REQUIRE_LATEST_DEVELOP:-false}"
github_output="${GITHUB_OUTPUT:?GITHUB_OUTPUT is required}"
max_attempts=3

# 이 스크립트는 쓰기 권한을 가진 Publisher에서 실행되므로 환경변수도 신뢰하지 않고 먼저
# 제한한다. 특히 PR 번호는 이후 디렉터리 경로가 되므로 양의 정수 이외의 값을 허용하지 않는다.
case "$operation" in
  root)
    [[ -d "$site_dir" && "$source_sha" =~ ^[0-9a-f]{40}$ && "$require_latest_develop" =~ ^(true|false)$ ]]
    ;;
  preview)
    [[ -d "$site_dir" && "$pr_number" =~ ^[1-9][0-9]*$ && "$source_sha" =~ ^[0-9a-f]{40}$ ]]
    [[ "$require_latest_develop" == "false" ]]
    ;;
  delete-preview)
    [[ "$pr_number" =~ ^[1-9][0-9]*$ ]]
    [[ "$require_latest_develop" == "false" ]]
    ;;
  *)
    echo "지원하지 않는 Catalog snapshot 작업입니다: $operation" >&2
    exit 1
    ;;
esac

# 호출 workflow가 배포 여부와 댓글 상태를 구분할 수 있도록 실제 원격 변경 여부를 항상
# 출력한다. no-op 삭제나 오래된 자동 root build는 성공적으로 건너뛰되 배포 artifact는 만들지 않는다.
write_snapshot_result() {
  local changed="$1"
  local sha="${2:-}"
  {
    echo "snapshot_changed=$changed"
    echo "snapshot_sha=$sha"
  } >> "$github_output"
}

# 자동 root 배포는 현재 develop HEAD만 허용한다. Publisher workflow에서 한 번 검증했더라도
# 대기·빌드 사이에 새 push가 생길 수 있어 실제 gh-pages 갱신 직전에도 원격을 다시 확인한다.
validate_latest_develop() {
  local latest_develop_sha
  latest_develop_sha="$(git ls-remote --heads origin refs/heads/develop | awk 'NR == 1 { print $1 }')"
  if [[ ! "$latest_develop_sha" =~ ^[0-9a-f]{40}$ ]]; then
    echo "최신 develop 원격 SHA를 확인할 수 없습니다." >&2
    return 2
  fi
  if [[ "$latest_develop_sha" != "$source_sha" ]]; then
    echo "더 최신인 develop commit이 있어 오래된 root 배포를 건너뜁니다: $source_sha" >&2
    return 1
  fi
}

skip_stale_root_if_needed() {
  local validation_status
  if [[ "$operation" != "root" || "$require_latest_develop" != "true" ]]; then
    return 0
  fi
  if validate_latest_develop; then
    return 0
  else
    validation_status=$?
  fi
  if [[ "$validation_status" -eq 1 ]]; then
    write_snapshot_result "false"
    exit 0
  fi
  exit "$validation_status"
}

# GitHub-hosted runner의 격리된 임시 경로만 사용한다. 아래 orphan checkout은 작업 tree를
# 비우므로 개발자의 일반 작업공간이 아니라 CI checkout에서만 실행해야 한다.
runner_temp="${RUNNER_TEMP:?RUNNER_TEMP is required}"
work_dir="$runner_temp/catalog-pages-snapshot-${GITHUB_RUN_ID:-$$}-${RANDOM}"
mkdir -p "$work_dir"
trap 'rm -rf -- "$work_dir"' EXIT

# gh-pages는 사람이 수정하는 소스 브랜치가 아니라 공개 snapshot 저장소다. commit 작성자를
# Actions bot으로 고정해 제품 코드 commit과 운영 snapshot을 명확히 구분한다.
git config user.name "github-actions[bot]"
git config user.email "41898282+github-actions[bot]@users.noreply.github.com"

for attempt in $(seq 1 "$max_attempts"); do
  old_dir="$work_dir/old-$attempt"
  next_dir="$work_dir/next-$attempt"
  mkdir -p "$old_dir" "$next_dir"

  # 직렬화 설정이 있더라도 재실행이나 수동 갱신이 겹칠 수 있다. 매 시도마다 최신 원격 SHA와
  # tree를 다시 읽어 lease 기준을 새로 잡고 다른 PR의 Preview를 보존한다.
  skip_stale_root_if_needed
  remote_sha="$(git ls-remote --heads origin refs/heads/gh-pages | awk 'NR == 1 { print $1 }')"
  if [[ -n "$remote_sha" ]]; then
    git fetch --no-tags origin "$remote_sha"
    git archive "$remote_sha" | tar -x -C "$old_dir"
  elif [[ "$operation" == "delete-preview" ]]; then
    # 최초 root 배포보다 PR 종료 이벤트가 먼저 도착할 수 있다. 삭제할 사이트 자체가 없으면
    # 이미 원하는 상태이므로 오류나 빈 gh-pages branch를 만들지 않고 멱등 성공으로 끝낸다.
    write_snapshot_result "false"
    exit 0
  elif [[ "$operation" != "root" ]]; then
    echo "gh-pages root bootstrap이 완료되기 전에는 Preview를 변경할 수 없습니다." >&2
    exit 1
  fi

  # 기존 snapshot이 있다면 공개 manifest와 실제 PR 디렉터리가 함께 관리되는지 먼저 확인한다.
  # 불완전한 snapshot 위에 새 내용을 덮으면 다른 Preview까지 잃을 수 있으므로 복구를 우선한다.
  if [[ -n "$remote_sha" && -f "$old_dir/catalog-deployments.json" ]]; then
    jq -e '.root.commit | strings' "$old_dir/catalog-deployments.json" >/dev/null
    jq -e '.previews | objects' "$old_dir/catalog-deployments.json" >/dev/null
  elif [[ -n "$remote_sha" && -d "$old_dir/pr" ]]; then
    echo "기존 PR Preview와 catalog-deployments.json의 상태가 일치하지 않습니다." >&2
    exit 1
  fi

  if [[ "$operation" == "delete-preview" && -f "$old_dir/catalog-deployments.json" ]]; then
    preview_recorded="$(jq -r --arg pr "$pr_number" '.previews | has($pr)' "$old_dir/catalog-deployments.json")"
    if [[ "$preview_recorded" == "false" && ! -d "$old_dir/pr/$pr_number" ]]; then
      # 이미 제거된 Preview는 새 snapshot commit과 Pages 배포를 만들 필요가 없다.
      write_snapshot_result "false" "$remote_sha"
      exit 0
    fi
    if [[ "$preview_recorded" != "true" || ! -d "$old_dir/pr/$pr_number" ]]; then
      echo "PR #${pr_number} Preview 디렉터리와 배포 manifest 상태가 일치하지 않습니다." >&2
      exit 1
    fi
  fi

  # 각 이벤트는 자신이 소유한 경로만 교체한다. root 갱신은 모든 Preview를 보존하고 Preview
  # 갱신·삭제는 root 및 다른 PR 디렉터리를 그대로 복사한다.
  case "$operation" in
    root)
      cp -R "$site_dir/." "$next_dir/"
      rm -rf -- "$next_dir/pr"
      if [[ -d "$old_dir/pr" ]]; then
        cp -R "$old_dir/pr" "$next_dir/pr"
      fi
      if [[ -f "$old_dir/catalog-deployments.json" ]]; then
        jq --arg sha "$source_sha" \
          '.root = {commit: $sha} | .previews = (.previews // {})' \
          "$old_dir/catalog-deployments.json" > "$next_dir/catalog-deployments.json"
      else
        jq -n --arg sha "$source_sha" \
          '{root: {commit: $sha}, previews: {}}' > "$next_dir/catalog-deployments.json"
      fi
      commit_message="chore: deploy catalog root ${source_sha}"
      ;;
    preview)
      test -s "$old_dir/index.html"
      test -f "$old_dir/catalog-deployments.json"
      cp -R "$old_dir/." "$next_dir/"
      rm -rf -- "$next_dir/pr/$pr_number"
      mkdir -p "$next_dir/pr/$pr_number"
      cp -R "$site_dir/." "$next_dir/pr/$pr_number/"
      jq --arg pr "$pr_number" --arg sha "$source_sha" \
        '.previews[$pr] = {commit: $sha}' \
        "$old_dir/catalog-deployments.json" > "$next_dir/catalog-deployments.json.tmp"
      mv "$next_dir/catalog-deployments.json.tmp" "$next_dir/catalog-deployments.json"
      commit_message="chore: deploy catalog preview PR #${pr_number} ${source_sha}"
      ;;
    delete-preview)
      test -s "$old_dir/index.html"
      test -f "$old_dir/catalog-deployments.json"
      cp -R "$old_dir/." "$next_dir/"
      rm -rf -- "$next_dir/pr/$pr_number"
      jq --arg pr "$pr_number" \
        'del(.previews[$pr])' \
        "$old_dir/catalog-deployments.json" > "$next_dir/catalog-deployments.json.tmp"
      mv "$next_dir/catalog-deployments.json.tmp" "$next_dir/catalog-deployments.json"
      commit_message="chore: remove catalog preview PR #${pr_number}"
      ;;
  esac

  # gh-pages는 공개 저장소와 동일하게 취급한다. 일반 파일·디렉터리 외 항목, 인증 자료,
  # source map을 차단해 PR artifact가 Publisher의 쓰기 권한을 통해 그대로 노출되지 않게 한다.
  touch "$next_dir/.nojekyll"
  if find "$next_dir" ! -type f ! -type d -print -quit | grep -q .; then
    echo "Catalog snapshot에는 일반 파일과 디렉터리 외의 항목을 포함할 수 없습니다." >&2
    exit 1
  fi
  if find "$next_dir" -type d -name '.git' -print -quit | grep -q .; then
    echo "Catalog snapshot에는 중첩 Git 저장소를 포함할 수 없습니다." >&2
    exit 1
  fi
  if find "$next_dir" -type f \( -name 'local.properties' -o -name '*.jks' -o -name '*.keystore' \) -print -quit | grep -q .; then
    echo "Catalog snapshot에서 로컬 또는 인증 설정 파일이 발견됐습니다." >&2
    exit 1
  fi
  if find "$next_dir" -type f -name '*.map' -print -quit | grep -q .; then
    echo "Catalog snapshot에는 source map을 포함할 수 없습니다." >&2
    exit 1
  fi
  jq -e '.root.commit | strings' "$next_dir/catalog-deployments.json" >/dev/null
  jq -e '.previews | objects' "$next_dir/catalog-deployments.json" >/dev/null

  # 전체 사이트를 부모 없는 단일 commit으로 만들면 이전 대형 Wasm 파일의 이력이 누적되지
  # 않는다. force-with-lease는 위에서 읽은 원격 SHA가 그대로일 때만 이 snapshot을 반영한다.
  snapshot_branch="catalog-pages-snapshot-${GITHUB_RUN_ID:-$$}-$attempt"
  git checkout --orphan "$snapshot_branch"
  git rm -rf --ignore-unmatch .
  find . -mindepth 1 -maxdepth 1 ! -name .git -exec rm -rf -- {} +
  cp -R "$next_dir/." .
  git add -A
  git commit -m "$commit_message"

  # tree를 만드는 동안 develop이 바뀌었으면 오래된 자동 root를 게시하지 않는다. 수동 root
  # rollback은 사용자가 선택한 과거 develop commit을 의도적으로 배포하므로 이 검사를 생략한다.
  skip_stale_root_if_needed

  if [[ -z "$remote_sha" ]]; then
    if git push origin HEAD:refs/heads/gh-pages; then
      push_succeeded=true
    else
      push_succeeded=false
    fi
  elif git push \
    --force-with-lease="refs/heads/gh-pages:$remote_sha" \
    origin HEAD:refs/heads/gh-pages; then
    push_succeeded=true
  else
    push_succeeded=false
  fi

  if [[ "$push_succeeded" == "true" ]]; then
    # push 직후에도 원격을 다시 읽는다. Pages artifact를 로컬 예상 tree가 아니라 실제 원격
    # snapshot에서 만들면 branch와 공개 배포가 서로 다른 commit을 가리키는 문제를 줄일 수 있다.
    latest_remote_sha="$(git ls-remote --heads origin refs/heads/gh-pages | awk 'NR == 1 { print $1 }')"
    if [[ ! "$latest_remote_sha" =~ ^[0-9a-f]{40}$ ]]; then
      echo "갱신 후 gh-pages 원격 SHA를 확인할 수 없습니다." >&2
      exit 1
    fi
    git fetch --no-tags origin "$latest_remote_sha"
    rm -rf -- "$output_dir"
    mkdir -p "$output_dir"
    git archive "$latest_remote_sha" | tar -x -C "$output_dir"
    write_snapshot_result "true" "$latest_remote_sha"
    exit 0
  fi

  echo "gh-pages가 동시에 변경되어 snapshot 갱신을 다시 시도합니다. ($attempt/$max_attempts)" >&2
done

echo "gh-pages snapshot 갱신 재시도 횟수를 초과했습니다." >&2
exit 1
