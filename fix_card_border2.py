import re

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

# I will replace `border = if (com.example.ui.theme.LocalAppTheme.current == "electro") BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,`
# with nothing.

content = content.replace('border = if (com.example.ui.theme.LocalAppTheme.current == "electro") BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,\n            ', '')
content = content.replace('border = if (com.example.ui.theme.LocalAppTheme.current == "electro") BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,\n', '')

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
