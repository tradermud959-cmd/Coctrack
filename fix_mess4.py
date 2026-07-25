with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

bad_str = '    modifier = Modifier.animatedNeonBorder(com.example.ui.theme.LocalAppTheme.current == "electro", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary),'

content = content.replace(bad_str, '')

bad_str2 = '                    modifier = Modifier.animatedNeonBorder(com.example.ui.theme.LocalAppTheme.current == "electro", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary).padding(16.dp),'
content = content.replace(bad_str2, '                    modifier = Modifier.padding(16.dp),')


with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
