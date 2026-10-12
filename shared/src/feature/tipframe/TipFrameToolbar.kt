package com.ma7moud3ly.tipframe.feature.tipframe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.tipframe.feature.tipframe.model.TipFrameEvent
import com.ma7moud3ly.tipframe.platform.LocalPlatform
import com.ma7moud3ly.tipframe.platform.Platform
import com.ma7moud3ly.tipframe.platform.isWeb
import com.ma7moud3ly.tipframe.resources.Res
import com.ma7moud3ly.tipframe.resources.app_nemo_link
import com.ma7moud3ly.tipframe.resources.app_nemo_name
import com.ma7moud3ly.tipframe.resources.app_powered_by
import com.ma7moud3ly.tipframe.resources.app_repo_link
import com.ma7moud3ly.tipframe.resources.app_wordmark
import com.ma7moud3ly.tipframe.resources.toolbar_copy_image
import com.ma7moud3ly.tipframe.resources.toolbar_copy_link
import com.ma7moud3ly.tipframe.resources.toolbar_export
import com.ma7moud3ly.tipframe.resources.toolbar_github
import com.ma7moud3ly.tipframe.resources.toolbar_reset
import com.ma7moud3ly.tipframe.resources.toolbar_theme_dark
import com.ma7moud3ly.tipframe.resources.toolbar_theme_light
import com.ma7moud3ly.tipframe.ui.components.ToolbarButton
import com.ma7moud3ly.tipframe.ui.theme.AppTheme
import com.ma7moud3ly.tipframe.ui.theme.LocalEditorTheme
import com.ma7moud3ly.tipframe.ui.theme.codeFontFamily
import org.jetbrains.compose.resources.stringResource

@Preview
@Composable
private fun TipFrameToolbarPreviewDark() {
    AppTheme(darkTheme = true) {
        TipFrameToolbar(showLabels = true, uiEvents = {})
    }
}

@Preview
@Composable
private fun TipFrameToolbarPreviewDarkWeb() {
    CompositionLocalProvider(LocalPlatform provides Platform.Web) {
        AppTheme(darkTheme = true) {
            TipFrameToolbar(showLabels = true, uiEvents = {})
        }
    }
}

@Preview
@Composable
private fun TipFrameToolbarPreviewLightCompact() {
    AppTheme(darkTheme = false) {
        TipFrameToolbar(showLabels = false, uiEvents = {})
    }
}

/**
 * The top bar: the wordmark with a credit to nemo-editor under it, then the link to the project's repository, the
 * dark/light toggle, reset, copy link (on the web only), copy image and export.
 *
 * @param showLabels whether the buttons show their text beside their icon
 */
@Composable
internal fun TipFrameToolbar(
    showLabels: Boolean,
    uiEvents: (TipFrameEvent) -> Unit
) {
    val isDark = LocalEditorTheme.current.dark
    val isWeb = LocalPlatform.current.isWeb
    val uriHandler = LocalUriHandler.current
    val repoLink = stringResource(Res.string.app_repo_link)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .height(56.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(Res.string.app_wordmark),
                fontFamily = codeFontFamily(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
            PoweredByNemo()
        }
        Spacer(Modifier.weight(1f))
        ToolbarButton(
            icon = Icons.Outlined.Code,
            label = stringResource(Res.string.toolbar_github),
            showLabel = showLabels,
            onClick = { uriHandler.openUri(repoLink) }
        )
        ToolbarButton(
            icon = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
            label = stringResource(
                if (isDark) Res.string.toolbar_theme_light else Res.string.toolbar_theme_dark
            ),
            showLabel = false,
            onClick = { uiEvents(TipFrameEvent.ToggleAppTheme) }
        )
        ToolbarButton(
            icon = Icons.Outlined.Refresh,
            label = stringResource(Res.string.toolbar_reset),
            showLabel = false,
            onClick = { uiEvents(TipFrameEvent.Reset) }
        )
        // only the web app has an address to share
        if (isWeb) ToolbarButton(
            icon = Icons.Outlined.Link,
            label = stringResource(Res.string.toolbar_copy_link),
            showLabel = showLabels,
            onClick = { uiEvents(TipFrameEvent.CopyLink) }
        )
        ToolbarButton(
            icon = Icons.Outlined.ContentCopy,
            label = stringResource(Res.string.toolbar_copy_image),
            showLabel = showLabels,
            onClick = { uiEvents(TipFrameEvent.CopyImage) }
        )
        ToolbarButton(
            icon = Icons.Outlined.Download,
            label = stringResource(Res.string.toolbar_export),
            showLabel = showLabels,
            filled = true,
            onClick = { uiEvents(TipFrameEvent.Export) }
        )
    }
}

/** Credit and link to the editor library the code frame is built with, in small print under the wordmark. */
@Composable
private fun PoweredByNemo() {
    val uriHandler = LocalUriHandler.current
    val link = stringResource(Res.string.app_nemo_link)
    Row(
        modifier = Modifier.clickable { uriHandler.openUri(link) },
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.app_powered_by),
            fontSize = 9.sp,
            lineHeight = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Text(
            text = stringResource(Res.string.app_nemo_name),
            fontSize = 9.sp,
            lineHeight = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}
