import re

with open('./app/src/main/java/com/example/data/api/AiApiService.kt', 'r') as f:
    content = f.read()

replacement = """import com.squareup.moshi.Json

data class GeminiRequest(
    @Json(name = "system_instruction") val systemInstruction: GeminiContent? = null,
    val contents: List<GeminiContent>
)"""

content = re.sub(r'data class GeminiRequest\(\s*val contents: List<GeminiContent>\s*\)', replacement, content)

with open('./app/src/main/java/com/example/data/api/AiApiService.kt', 'w') as f:
    f.write(content)
