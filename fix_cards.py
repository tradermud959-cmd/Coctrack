import re

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

modifier_code = """
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.Stroke

fun Modifier.animatedNeonBorder(
    isElectro: Boolean,
    primaryColor: Color,
    secondaryColor: Color,
    cornerRadius: androidx.compose.ui.unit.Dp = 16.dp,
    borderWidth: androidx.compose.ui.unit.Dp = 2.dp
): Modifier = composed {
    if (!isElectro) return@composed this

    val infiniteTransition = rememberInfiniteTransition(label = "neon_rotation")
    val offsetProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "neon_offset"
    )

    this.then(
        Modifier.drawWithContent {
            drawContent()
            val strokeWidthPx = borderWidth.toPx()
            val radiusPx = cornerRadius.toPx()
            
            // We can simulate rotation by animating colors
            // Instead of true rotation, let's just make a pulsing/moving linear gradient
            
            val startX = if (offsetProgress < 0.5f) offsetProgress * 2 * size.width else (1f - (offsetProgress - 0.5f) * 2) * size.width
            val startY = if (offsetProgress < 0.5f) 0f else size.height
            val endX = size.width - startX
            val endY = size.height - startY
            
            val brush = Brush.linearGradient(
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
"""

content = content.replace("package com.example.ui.screens", "package com.example.ui.screens\n\n" + modifier_code)

content = re.sub(r'(\bCard\(\n\s*modifier = Modifier)', r'\1.animatedNeonBorder(com.example.ui.theme.LocalAppTheme.current == "electro", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)', content)

# There are other Card modifiers
content = re.sub(r'(\bCard\(\n\s*shape = RoundedCornerShape\([^\)]+\),\n\s*modifier = Modifier)', r'\1.animatedNeonBorder(com.example.ui.theme.LocalAppTheme.current == "electro", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)', content)

# Just replace `Card(` and add the modifier inside its children maybe? No, `animatedNeonBorder` goes on Card's modifier.
# Let's use re.sub for all Card usages that have `modifier = Modifier`
# We'll just replace `modifier = Modifier\n` with `modifier = Modifier.animatedNeonBorder(com.example.ui.theme.LocalAppTheme.current == "electro", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)\n` 
# only for Cards. Wait, this might affect non-Cards.
