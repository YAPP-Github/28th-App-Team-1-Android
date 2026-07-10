package com.dminus14.app.core.permission

/**
 * 앱 런타임 권한 조회 API.
 */
interface PermissionManager {
    /** 권한 허용 여부를 확인한다. */
    fun isGranted(permission: AppPermission): Boolean

    /**
     * 권한 설명(rationale)을 표시해야 하는지 확인한다.
     * 1회 거부이력이 있는 사용자의 경우 true 반환
     * -> 기획서는 최초 1회만 시스템 팝업노출로 가져갈 수 있도록.. 만약 안된다면 수정 필요
     * Activity의 [android.app.Activity.shouldShowRequestPermissionRationale] 결과를 넘긴다.
     */
    fun shouldShowRationale(
        permission: AppPermission,
        shouldShowRequestPermissionRationale: (manifestPermission: String) -> Boolean,
    ): Boolean
}
