with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    for i, line in enumerate(f):
        if line.startswith('import '):
            print(line, end='')
