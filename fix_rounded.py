import re

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

# Fix the shape = RoundedCornerShape(xx.dp, -> shape = RoundedCornerShape(xx.dp),
# Only if it's the end of line or followed by `\n` or `colors` or `elevation` etc.
# Actually, the regex `Card\((.*?)\)` consumed the `)`. So `16.dp` was followed by `,` if it originally had `,`. Or `\n` if it didn't.
# If it had `,`, it became `16.dp,`.
# If it didn't have `,`, it became `16.dp\n`.

# We can just look for `RoundedCornerShape([0-9]+\.dp)` NO wait! It is missing `)`!
# It looks like `RoundedCornerShape(16.dp,` or `RoundedCornerShape(16.dp\n`

content = re.sub(r'RoundedCornerShape\(([0-9]+\.dp),', r'RoundedCornerShape(\1),', content)
content = re.sub(r'RoundedCornerShape\(([0-9]+\.dp)\n', r'RoundedCornerShape(\1)\n', content)

# But wait, did it consume `)` for other things? Like `Card(modifier = Modifier.padding(16.dp))`
# This would become `Card(modifier = Modifier.animatedNeonBorder(...).padding(16.dp`!
# Let's fix that too.
content = re.sub(r'\.padding\(([0-9]+\.dp),', r'.padding(\1),', content)
content = re.sub(r'\.padding\(([0-9]+\.dp)\n', r'.padding(\1)\n', content)

# Also check for `.fillMaxWidth(),` -> regex matched `.fillMaxWidth()` and consumed `)`.
content = re.sub(r'\.fillMaxWidth\(\s*,', r'.fillMaxWidth(),', content)
content = re.sub(r'\.fillMaxWidth\(\s*\n', r'.fillMaxWidth()\n', content)

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
