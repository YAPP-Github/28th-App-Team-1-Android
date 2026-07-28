package com.dminus14.designsystem.component.icon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dminus14.app.core.resources.Res
import com.dminus14.app.core.resources.add
import com.dminus14.app.core.resources.ai
import com.dminus14.app.core.resources.cancel
import com.dminus14.app.core.resources.check
import com.dminus14.app.core.resources.coupon
import com.dminus14.app.core.resources.delete
import com.dminus14.app.core.resources.edit
import com.dminus14.app.core.resources.expand
import com.dminus14.app.core.resources.eye
import com.dminus14.app.core.resources.file
import com.dminus14.app.core.resources.hand
import com.dminus14.app.core.resources.left
import com.dminus14.app.core.resources.pause
import com.dminus14.app.core.resources.play
import com.dminus14.app.core.resources.profile
import com.dminus14.app.core.resources.right
import com.dminus14.app.core.resources.script
import com.dminus14.app.core.resources.skip_left
import com.dminus14.app.core.resources.skip_right
import com.dminus14.app.core.resources.timer
import com.dminus14.app.core.resources.video
import com.dminus14.app.core.resources.warning
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class HilitIconAsset(
    val resourceName: String,
    internal val resource: DrawableResource,
) {
    Add("add", Res.drawable.add),
    Ai("ai", Res.drawable.ai),
    Cancel("cancel", Res.drawable.cancel),
    Check("check", Res.drawable.check),
    Coupon("coupon", Res.drawable.coupon),
    Delete("delete", Res.drawable.delete),
    Edit("edit", Res.drawable.edit),
    Eye("eye", Res.drawable.eye),
    File("file", Res.drawable.file),
    Expand("expand", Res.drawable.expand),
    Hand("hand", Res.drawable.hand),
    Left("left", Res.drawable.left),
    Pause("pause", Res.drawable.pause),
    Play("play", Res.drawable.play),
    Profile("profile", Res.drawable.profile),
    Right("right", Res.drawable.right),
    Script("script", Res.drawable.script),
    SkipLeft("skip_left", Res.drawable.skip_left),
    SkipRight("skip_right", Res.drawable.skip_right),
    Timer("timer", Res.drawable.timer),
    Video("video", Res.drawable.video),
    Warning("warning", Res.drawable.warning),
}

@Composable
fun HilitIcon(
    asset: HilitIconAsset,
    contentDescription: String?,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(asset.resource),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier,
    )
}
