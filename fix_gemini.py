import re

with open('./app/src/main/java/com/example/ui/viewmodel/GemsViewModel.kt', 'r') as f:
    content = f.read()

replacement1 = """                if (provider == "gemini") {
                    // Combine consecutive messages with the same role
                    val contents = mutableListOf<com.example.data.api.GeminiContent>()
                    for (msg in _chatMessages.value) {
                        val role = if (msg.role == "user") "user" else "model"
                        if (contents.isNotEmpty() && contents.last().role == role) {
                            val lastPartText = contents.last().parts.first().text
                            contents[contents.size - 1] = contents.last().copy(
                                parts = listOf(com.example.data.api.GeminiPart(text = lastPartText + "\\n" + msg.content))
                            )
                        } else {
                            contents.add(
                                com.example.data.api.GeminiContent(
                                    role = role,
                                    parts = listOf(com.example.data.api.GeminiPart(text = msg.content))
                                )
                            )
                        }
                    }
                    
                    val req = com.example.data.api.GeminiRequest(
                        systemInstruction = com.example.data.api.GeminiContent(
                            parts = listOf(com.example.data.api.GeminiPart(text = systemInstruction))
                        ),
                        contents = contents
                    )"""

# using simple string replace
start_idx = content.find('if (provider == "gemini") {')
end_idx = content.find('val url = "v1beta/models/gemini-1.5-flash:generateContent"', start_idx)
content = content[:start_idx] + replacement1 + '\n                    ' + content[end_idx:]

with open('./app/src/main/java/com/example/ui/viewmodel/GemsViewModel.kt', 'w') as f:
    f.write(content)
