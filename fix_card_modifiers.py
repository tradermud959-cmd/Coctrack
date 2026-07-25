import re

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

# Make sure we don't double add animatedNeonBorder
content = content.replace(".animatedNeonBorder(com.example.ui.theme.LocalAppTheme.current == \"electro\", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)", "")

# Find all Cards and append animatedNeonBorder to their modifier.
# The safest way is to find `Card(` and the next `modifier = Modifier` inside it.
def card_replacer(match):
    inner = match.group(1)
    if "modifier = Modifier" in inner:
        # replace the first occurrence
        inner = inner.replace("modifier = Modifier", "modifier = Modifier.animatedNeonBorder(com.example.ui.theme.LocalAppTheme.current == \"electro\", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)", 1)
    else:
        # insert modifier
        if "shape" in inner or "colors" in inner:
            inner = inner.replace("\n", "\n    modifier = Modifier.animatedNeonBorder(com.example.ui.theme.LocalAppTheme.current == \"electro\", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary),", 1)
        else:
            inner = inner + "modifier = Modifier.animatedNeonBorder(com.example.ui.theme.LocalAppTheme.current == \"electro\", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary),"
    return f"Card({inner}"

content = re.sub(r'Card\((.*?)\)', card_replacer, content, flags=re.DOTALL)

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
