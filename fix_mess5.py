with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("@OptIn(ExperimentalMaterial3Api::class)\n@Composable\n@Composable\nfun NeonCard", "@Composable\nfun NeonCard")
content = content.replace("@Composable\n@Composable", "@Composable")

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
