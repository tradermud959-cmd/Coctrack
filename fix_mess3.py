with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'r') as f:
    content = f.read()

# I want to add `NeonCard` composable:
neon_card = """
@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.animatedNeonBorder(
            isElectro = com.example.ui.theme.LocalAppTheme.current == "electro",
            primaryColor = MaterialTheme.colorScheme.primary,
            secondaryColor = MaterialTheme.colorScheme.secondary
        ),
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content
    )
}
"""

if "fun NeonCard" not in content:
    content = content.replace("fun MainAppScreen", neon_card + "\n@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun MainAppScreen")

# Now, we replace `Card(` with `NeonCard(` in the whole file EXCEPT in the NeonCard definition.
content = content.replace("    Card(", "    NeonCard(")
content = content.replace("                Card(", "                NeonCard(")
content = content.replace("            Card(", "            NeonCard(")
content = content.replace("        Card(", "        NeonCard(")
# Ensure NeonCard definition doesn't use NeonCard
content = content.replace("    NeonCard(\n        modifier = modifier.animatedNeonBorder", "    Card(\n        modifier = modifier.animatedNeonBorder")

with open('./app/src/main/java/com/example/ui/screens/MainAppScreen.kt', 'w') as f:
    f.write(content)
