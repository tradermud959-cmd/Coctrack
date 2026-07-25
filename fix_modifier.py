import re

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

# Remove imports from bottom
content = content.replace("import androidx.compose.ui.composed\nimport androidx.compose.ui.draw.drawWithContent\nimport androidx.compose.ui.graphics.drawscope.Stroke", "")

# Add imports to top
import_str = "import androidx.compose.ui.composed\nimport androidx.compose.ui.draw.drawWithContent\nimport androidx.compose.ui.graphics.drawscope.Stroke\n"
content = content.replace("import androidx.compose.ui.Alignment", import_str + "import androidx.compose.ui.Alignment")

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
