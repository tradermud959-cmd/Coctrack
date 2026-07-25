with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
skip = False
for i, line in enumerate(lines):
    if line.strip() == "@Composable" and i < len(lines)-1 and lines[i+1].strip() == "@Composable":
        continue # Skip the duplicate
    new_lines.append(line)

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.writelines(new_lines)
