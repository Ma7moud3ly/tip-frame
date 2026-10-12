package com.ma7moud3ly.tipframe.feature.tipframe.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ma7moud3ly.tipframe.model.AuthorAvatar
import com.ma7moud3ly.tipframe.ui.theme.AppThemes
import com.ma7moud3ly.tipframe.ui.theme.accent
import io.ma7moud3ly.nemo.model.EditorTheme

/** Diameter of the avatar. */
private val AvatarSize = 22.dp

@Preview
@Composable
private fun AuthorBadgePreviewDark() {
    AuthorBadgePreview(AppThemes.DEFAULT_DARK)
}

@Preview
@Composable
private fun AuthorBadgePreviewLight() {
    AuthorBadgePreview(AppThemes.DEFAULT_LIGHT)
}

/** Every avatar, one badge under the other. */
@Composable
private fun AuthorBadgePreview(theme: EditorTheme) {
    Column {
        AuthorAvatar.entries.forEach { avatar ->
            AuthorBadge(
                avatar = avatar,
                name = "Mahmoud Aly",
                handle = "ma7moud3ly",
                theme = theme
            )
        }
    }
}

/**
 * The author under the code, as a slim bar like an editor's status bar: a
 * line across the top, then the [avatar] with the name and, right under it,
 * the handle.
 *
 * Shows nothing when [avatar] is [AuthorAvatar.NONE] or both [name] and
 * [handle] are blank.
 */
@Composable
internal fun AuthorBadge(
    avatar: AuthorAvatar,
    name: String,
    handle: String,
    theme: EditorTheme,
    modifier: Modifier = Modifier
) {
    if (avatar == AuthorAvatar.NONE) return
    if (name.isBlank() && handle.isBlank()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(theme.gutter))
    ) {
        // the line that separates the bar from the code
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(if (theme.dark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.10f))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                avatar = avatar,
                initials = initials(name.ifBlank { handle }),
                theme = theme
            )
            Spacer(Modifier.width(8.dp))
            // the handle sits right under the name, with no space between the two lines
            Column {
                if (name.isNotBlank()) Text(
                    text = name,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(theme.foreground),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (handle.isNotBlank()) Text(
                    text = "@" + handle.trim().removePrefix("@"),
                    fontSize = 8.sp,
                    lineHeight = 11.sp,
                    // a quieter shade of the text color, so the name stays the first thing read
                    color = Color(theme.foreground).copy(alpha = 0.55f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * The avatar: the author's [initials] on the theme's accent color, or the
 * mark of a social network on that network's color.
 */
@Composable
private fun Avatar(
    avatar: AuthorAvatar,
    initials: String,
    theme: EditorTheme
) {
    when (avatar) {
        AuthorAvatar.NONE -> Unit

        AuthorAvatar.INITIALS -> AvatarBox(CircleShape, SolidColor(theme.accent)) {
            MarkText(text = initials, size = 9, color = Color(theme.background))
        }

        AuthorAvatar.LINKEDIN -> AvatarBox(RoundedCornerShape(5.dp), SolidColor(Color(0xFF0A66C2))) {
            MarkText(text = "in", size = 11)
        }

        AuthorAvatar.FACEBOOK -> AvatarBox(CircleShape, SolidColor(Color(0xFF1877F2))) {
            MarkText(text = "f", size = 13)
        }

        AuthorAvatar.X -> AvatarBox(RoundedCornerShape(5.dp), SolidColor(Color.Black)) {
            Canvas(modifier = Modifier.size(9.dp)) {
                val stroke = 1.5.dp.toPx()
                drawLine(Color.White, Offset.Zero, Offset(size.width, size.height), stroke)
                drawLine(Color.White, Offset(size.width, 0f), Offset(0f, size.height), stroke)
            }
        }

        AuthorAvatar.INSTAGRAM -> AvatarBox(RoundedCornerShape(6.dp), InstagramBrush) {
            Canvas(modifier = Modifier.size(12.dp)) {
                val stroke = Stroke(width = 1.2.dp.toPx())
                // the camera: its body, its lens and its flash
                drawRoundRect(
                    color = Color.White,
                    cornerRadius = CornerRadius(3.2.dp.toPx()),
                    style = stroke
                )
                drawCircle(color = Color.White, radius = size.minDimension * 0.24f, style = stroke)
                drawCircle(
                    color = Color.White,
                    radius = 0.8.dp.toPx(),
                    center = Offset(size.width * 0.78f, size.height * 0.22f)
                )
            }
        }
    }
}

/** The colors of the Instagram mark, from its bottom left to its top right. */
private val InstagramBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFEDA75),
        Color(0xFFFA7E1E),
        Color(0xFFD62976),
        Color(0xFF962FBF),
        Color(0xFF4F5BD5)
    ),
    start = Offset(0f, Float.POSITIVE_INFINITY),
    end = Offset(Float.POSITIVE_INFINITY, 0f)
)

/** The filled shape every avatar is drawn in. */
@Composable
private fun AvatarBox(
    shape: Shape,
    brush: Brush,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(AvatarSize)
            .clip(shape)
            .background(brush),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/** Bold text inside an avatar, [size] in sp. */
@Composable
private fun MarkText(
    text: String,
    size: Int,
    color: Color = Color.White
) {
    Text(
        text = text,
        fontSize = size.sp,
        lineHeight = size.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

/** The first letters of the first two words of [text], in upper case. */
private fun initials(text: String): String =
    text.trim().removePrefix("@")
        .split(' ')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
