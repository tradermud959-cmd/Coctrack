with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

bad_str = "@OptIn(ExperimentalMaterial3Api::class)@Composable@Composablefun NeonCard("
good_str = "@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun NeonCard("

content = content.replace(bad_str, good_str)
content = content.replace("@Composable@Composablefun", "@Composable\nfun")
content = content.replace("@OptIn(ExperimentalMaterial3Api::class)@Composable", "@OptIn(ExperimentalMaterial3Api::class)\n@Composable\n")

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
