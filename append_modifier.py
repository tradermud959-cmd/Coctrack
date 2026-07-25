with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'a') as f:
    f.write('''
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.Stroke

fun Modifier.animatedNeonBorder(
    isElectro: Boolean,
    primaryColor: androidx.compose.ui.graphics.Color,
    secondaryColor: androidx.compose.ui.graphics.Color,
    cornerRadius: androidx.compose.ui.unit.Dp = 16.dp,
    borderWidth: androidx.compose.ui.unit.Dp = 2.dp
): Modifier = composed {
    if (!isElectro) return@composed this

    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "neon_rotation")
    val offsetProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(3000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "neon_offset"
    )

    this.then(
        androidx.compose.ui.Modifier.drawWithContent {
            drawContent()
            val strokeWidthPx = borderWidth.toPx()
            val radiusPx = cornerRadius.toPx()
            
            val startX = if (offsetProgress < 0.5f) offsetProgress * 2 * size.width else (1f - (offsetProgress - 0.5f) * 2) * size.width
            val startY = if (offsetProgress < 0.5f) 0f else size.height
            val endX = size.width - startX
            val endY = size.height - startY
            
            val brush = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(primaryColor, secondaryColor, primaryColor),
                start = androidx.compose.ui.geometry.Offset(startX, startY),
                end = androidx.compose.ui.geometry.Offset(endX, endY)
            )

            drawRoundRect(
                brush = brush,
                topLeft = androidx.compose.ui.geometry.Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = androidx.compose.ui.geometry.Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radiusPx, radiusPx),
                style = Stroke(strokeWidthPx)
            )
        }
    )
}
''')
