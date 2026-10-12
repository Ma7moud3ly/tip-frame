package com.ma7moud3ly.tipframe.feature.tipframe.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ma7moud3ly.tipframe.feature.tipframe.model.TipFrameEvent
import com.ma7moud3ly.tipframe.model.AuthorAvatar
import com.ma7moud3ly.tipframe.model.TipFrameConfig
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.author_handle
import com.ma7moud3ly.tipframe.resources.author_name
import com.ma7moud3ly.tipframe.resources.author_watermark
import com.ma7moud3ly.tipframe.resources.avatar_facebook
import com.ma7moud3ly.tipframe.resources.avatar_initials
import com.ma7moud3ly.tipframe.resources.avatar_instagram
import com.ma7moud3ly.tipframe.resources.avatar_linkedin
import com.ma7moud3ly.tipframe.resources.avatar_none
import com.ma7moud3ly.tipframe.resources.avatar_x
import com.ma7moud3ly.tipframe.resources.tools_author
import com.ma7moud3ly.tipframe.ui.components.MyDialog
import com.ma7moud3ly.tipframe.ui.components.ToolChip
import com.ma7moud3ly.tipframe.ui.components.ToolSwitch
import com.ma7moud3ly.tipframe.ui.components.ToolTextField
import com.ma7moud3ly.tipframe.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun AuthorOptionsPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            AuthorOptions(
                config = TipFrameConfig(
                    authorAvatar = AuthorAvatar.LINKEDIN,
                    authorName = "Mahmoud Aly",
                    authorHandle = "ma7moud3ly"
                ),
                uiEvents = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun AuthorOptionsPreviewLightNone() {
    AppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            AuthorOptions(
                config = TipFrameConfig(),
                uiEvents = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

/**
 * Dialog with the author badge's options: what its avatar shows, and the name
 * and handle beside it. It also holds the switch of the app's watermark. The name and handle fields are disabled while the
 * avatar is None, so the dialog keeps its size. Each change is applied at
 * once; the dialog stays open until it is closed.
 */
@Composable
internal fun AuthorDialog(
    config: TipFrameConfig,
    uiEvents: (TipFrameEvent) -> Unit,
    onDismiss: () -> Unit
) {
    MyDialog(
        title = stringResource(Res.string.tools_author),
        onDismiss = onDismiss,
        maxWidth = 440.dp
    ) {
        AuthorOptions(
            config = config,
            uiEvents = uiEvents,
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AuthorOptions(
    config: TipFrameConfig,
    uiEvents: (TipFrameEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AuthorAvatar.entries.forEach { avatar ->
                ToolChip(
                    text = stringResource(
                        when (avatar) {
                            AuthorAvatar.NONE -> Res.string.avatar_none
                            AuthorAvatar.INITIALS -> Res.string.avatar_initials
                            AuthorAvatar.LINKEDIN -> Res.string.avatar_linkedin
                            AuthorAvatar.X -> Res.string.avatar_x
                            AuthorAvatar.INSTAGRAM -> Res.string.avatar_instagram
                            AuthorAvatar.FACEBOOK -> Res.string.avatar_facebook
                        }
                    ),
                    selected = avatar == config.authorAvatar,
                    onClick = { uiEvents(TipFrameEvent.SelectAuthorAvatar(avatar)) }
                )
            }
        }

        // with no avatar there is no badge to fill in, so the fields stay but take no input
        val hasBadge = config.authorAvatar != AuthorAvatar.NONE
        ToolTextField(
            value = config.authorName,
            onValueChange = { uiEvents(TipFrameEvent.ChangeAuthorName(it)) },
            label = stringResource(Res.string.author_name),
            enabled = hasBadge
        )
        ToolTextField(
            value = config.authorHandle,
            onValueChange = { uiEvents(TipFrameEvent.ChangeAuthorHandle(it)) },
            label = stringResource(Res.string.author_handle),
            enabled = hasBadge
        )
        ToolSwitch(
            label = stringResource(Res.string.author_watermark),
            checked = config.watermark,
            onCheckedChange = { uiEvents(TipFrameEvent.ToggleWatermark) }
        )
    }
}
