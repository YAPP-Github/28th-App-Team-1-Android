package com.dminus14.app.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dminus14.app.feature.mypage.MyPageJobRole
import com.dminus14.app.feature.mypage.MyPageReportStatus
import com.dminus14.app.feature.mypage.MyPageReportUiModel
import com.dminus14.designsystem.component.icon.HilitIcon
import com.dminus14.designsystem.component.icon.HilitIconAsset
import com.dminus14.designsystem.component.tag.HilitTag
import com.dminus14.designsystem.component.tag.TagColorType
import com.dminus14.designsystem.component.tag.TagType
import com.dminus14.designsystem.theme.HilitTheme

@Composable
@Suppress("LongParameterList")
internal fun MyPageReportSection(
    reports: List<MyPageReportUiModel>,
    expandedReportIds: Set<String>,
    onReportToggleClick: (String) -> Unit,
    onReportViewClick: () -> Unit,
    onGuestFeedbackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "내 면접 레포트",
            style = HilitTheme.typography.body6,
            color = HilitTheme.colors.gray500,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        if (reports.isEmpty()) {
            Text(
                text = "내용 없음",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(HilitTheme.colors.hilitWhite)
                        .border(1.dp, HilitTheme.colors.gray100)
                        .padding(18.dp),
                style = HilitTheme.typography.body6,
                color = HilitTheme.colors.gray400,
            )
        } else {
            reports.forEach { report ->
                ReportCard(
                    report = report,
                    expanded = report.id in expandedReportIds,
                    onToggleClick = { onReportToggleClick(report.id) },
                    onReportViewClick = onReportViewClick,
                    onGuestFeedbackClick = onGuestFeedbackClick,
                )
            }
        }
    }
}

@Composable
@Suppress("LongMethod", "MagicNumber")
private fun ReportCard(
    report: MyPageReportUiModel,
    expanded: Boolean,
    onToggleClick: () -> Unit,
    onReportViewClick: () -> Unit,
    onGuestFeedbackClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    HilitTheme.colors.gray100,
                ).border(1.dp, HilitTheme.colors.gray100),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(color = HilitTheme.colors.hilitWhite)
                    .clickable(role = Role.Button, onClick = onToggleClick)
                    .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        "${report.jobRoleAndExperience}차 면접",
                        style = HilitTheme.typography.body2,
                        color = HilitTheme.colors.gray800,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        report.createdAt,
                        style = HilitTheme.typography.body9,
                        color = HilitTheme.colors.gray400,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    if (report.status == MyPageReportStatus.Failed) {
                        HilitTag(
                            colorType = TagColorType.Red,
                            tagType = TagType.Small,
                            text = "생성 실패",
                        )
                    }
                    HilitIcon(
                        asset = if (expanded) HilitIconAsset.Up else HilitIconAsset.Down,
                        contentDescription = if (expanded) "펼치기" else "접기",
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        if (expanded) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            HilitTheme.colors.gray50,
                        ).padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReportMetadataRow(
                        label = "직군 · 연차",
                        value = report.jobRoleAndExperience,
                    )
                    ReportMetadataRow(
                        label = "포트폴리오",
                        value = report.portfolioFileName,
                    )
                    ReportMetadataRow(
                        label = "JD",
                        value = report.jobDescription,
                    )
                }
                if (report.status == MyPageReportStatus.Completed) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReportAction(
                            text = "레포트 보기",
                            isPrimary = false,
                            onClick = onReportViewClick,
                            modifier = Modifier.weight(1f),
                        )
                        ReportAction(
                            text = "지인 피드백 받기",
                            isPrimary = true,
                            onClick = onGuestFeedbackClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportMetadataRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.width(70.dp),
            style = HilitTheme.typography.body9,
            color = HilitTheme.colors.gray400,
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = HilitTheme.typography.body9,
            color = HilitTheme.colors.gray700,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ReportAction(
    text: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier =
            modifier
                .background(
                    if (isPrimary) {
                        HilitTheme.colors.hilitBlack800
                    } else {
                        HilitTheme.colors.hilitWhite
                    },
                ).clickable(role = Role.Button, onClick = onClick)
                .padding(vertical = 8.dp),
        style = HilitTheme.typography.body5,
        color =
            if (isPrimary) {
                HilitTheme.colors.hilitWhite
            } else {
                HilitTheme.colors.gray700
            },
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun MyPageReportSectionPreview() {
    val report =
        MyPageReportUiModel(
            id = "sample-report",
            jobRole = MyPageJobRole.Android,
            experienceYears = 3,
            createdAt = "2026.08.04 14:20",
            status = MyPageReportStatus.Completed,
            portfolioFileName = "sample_portfolio.pdf",
            jobDescription = "careers.example.com/jobs/1024",
        )
    HilitTheme {
        MyPageReportSection(
            reports = listOf(report),
            expandedReportIds = setOf(report.id),
            onReportToggleClick = {},
            onReportViewClick = {},
            onGuestFeedbackClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun ReportCardPreview() {
    HilitTheme {
        ReportCard(
            report =
                MyPageReportUiModel(
                    id = "failed-report",
                    jobRole = MyPageJobRole.Ios,
                    experienceYears = 2,
                    createdAt = "2026.08.04 14:20",
                    status = MyPageReportStatus.Failed,
                    portfolioFileName = "sample_portfolio.pdf",
                    jobDescription = "JD 직접 입력",
                ),
            expanded = true,
            onToggleClick = {},
            onReportViewClick = {},
            onGuestFeedbackClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun ReportActionPreview() {
    HilitTheme {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ReportAction(
                text = "레포트 보기",
                isPrimary = false,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
            ReportAction(
                text = "지인 피드백 받기",
                isPrimary = true,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
        }
    }
}
