package com.dminus14.designsystem.component.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.ai_sparkle
import com.dminus14.app.core.resources.apple_logo
import com.dminus14.app.core.resources.cancel
import com.dminus14.app.core.resources.checkbox_check
import com.dminus14.app.core.resources.checkbox_uncheck
import com.dminus14.app.core.resources.coupon
import com.dminus14.app.core.resources.delete
import com.dminus14.app.core.resources.edit
import com.dminus14.app.core.resources.expand
import com.dminus14.app.core.resources.file
import com.dminus14.app.core.resources.fill_warning
import com.dminus14.app.core.resources.info
import com.dminus14.app.core.resources.kakao_logo
import com.dminus14.app.core.resources.left
import com.dminus14.app.core.resources.pause
import com.dminus14.app.core.resources.play
import com.dminus14.app.core.resources.plus
import com.dminus14.app.core.resources.profile
import com.dminus14.app.core.resources.right
import com.dminus14.app.core.resources.script
import com.dminus14.app.core.resources.skip_left
import com.dminus14.app.core.resources.skip_right
import com.dminus14.app.core.resources.success
import com.dminus14.app.core.resources.timer
import com.dminus14.app.core.resources.upload
import com.dminus14.app.core.resources.video
import com.dminus14.designsystem.theme.HilitTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class HilitIconAsset(
    val resourceName: String,
    val defaultSize: Dp,
    internal val resource: DrawableResource,
    internal val preservesOriginalColors: Boolean = false,
) {
    SkipLeft("skip_left", 24.dp, Res.drawable.skip_left),
    SkipRight("skip_right", 24.dp, Res.drawable.skip_right),
    Play("play", 24.dp, Res.drawable.play),
    Pause("pause", 24.dp, Res.drawable.pause),
    Video("video", 24.dp, Res.drawable.video),
    Timer("timer", 24.dp, Res.drawable.timer),
    Profile("profile", 24.dp, Res.drawable.profile),
    Edit("edit", 24.dp, Res.drawable.edit),
    Cancel("cancel", 24.dp, Res.drawable.cancel),
    Right("right", 24.dp, Res.drawable.right),
    Left("left", 24.dp, Res.drawable.left),
    Plus("plus", 24.dp, Res.drawable.plus),
    Success("success", 24.dp, Res.drawable.success),
    Expand("expand", 24.dp, Res.drawable.expand),
    File("file", 24.dp, Res.drawable.file),
    AiSparkle("ai_sparkle", 24.dp, Res.drawable.ai_sparkle),
    Script("script", 24.dp, Res.drawable.script),
    AppleLogo("apple_logo", 24.dp, Res.drawable.apple_logo, preservesOriginalColors = true),
    KakaoLogo("kakao_logo", 24.dp, Res.drawable.kakao_logo, preservesOriginalColors = true),
    CheckboxCheck("checkbox_check", 24.dp, Res.drawable.checkbox_check, preservesOriginalColors = true,),
    CheckboxUncheck("checkbox_uncheck", 24.dp, Res.drawable.checkbox_uncheck, preservesOriginalColors = true,),
    Coupon("coupon", 16.dp, Res.drawable.coupon),
    Info("info", 16.dp, Res.drawable.info),
    Delete("delete", 16.dp, Res.drawable.delete, preservesOriginalColors = true),
    FillWarning("fill_warning", 16.dp, Res.drawable.fill_warning, preservesOriginalColors = true),
    Upload("upload", 44.dp, Res.drawable.upload, preservesOriginalColors = true),
}

@Composable
fun HilitIcon(
    asset: HilitIconAsset,
    contentDescription: String?,
    tint: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(asset.resource),
        contentDescription = contentDescription,
        tint = if (asset.preservesOriginalColors) Color.Unspecified else tint,
        modifier = modifier,
    )
}

@Preview
@Composable
fun HilitIconPreview() {
    HilitTheme {
        HilitIcon(
            asset = HilitIconAsset.Plus,
            contentDescription = null,
        )
    }
}
