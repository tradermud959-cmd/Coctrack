import re

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

# 1. Remove the wrongly inserted `modifier = Modifier.animatedNeonBorder(...)`
# They look exactly like:
# `    modifier = Modifier.animatedNeonBorder(com.example.ui.theme.LocalAppTheme.current == "electro", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary),`
# with some whitespace.
content = re.sub(r'\s*modifier = Modifier\.animatedNeonBorder\(com\.example\.ui\.theme\.LocalAppTheme\.current == "electro", MaterialTheme\.colorScheme\.primary, MaterialTheme\.colorScheme\.secondary\),', '', content)

# Also remove `.animatedNeonBorder(...)` if it got attached to other Modifiers incorrectly, just to start fresh.
content = content.replace('.animatedNeonBorder(com.example.ui.theme.LocalAppTheme.current == "electro", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)', '')

# 2. Fix the `RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)` syntax errors from line 3004
# e: file:///app/applet/app/src/main/java/com/example/ui/screens/MainAppScreen.kt:3004:41 Syntax error: Expecting ')'.
# Let's see what is there around 3004
