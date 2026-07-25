package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.GemTransaction
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.GemsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String,
    val content: String
)

sealed interface AppScreen {
    object Splash : AppScreen
    object Dashboard : AppScreen
    object UpdateGems : AppScreen
    object Statistics : AppScreen
    object History : AppScreen
    object Target : AppScreen
    object OfflineAI : AppScreen
    object Settings : AppScreen
    object About : AppScreen
    object Partner : AppScreen
}

class GemsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val preferencesRepo = UserPreferencesRepository(application)
    private val repository = GemsRepository(db.gemDao, preferencesRepo)

    // UI Navigation Screen
    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Splash)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Preferences & Settings states
    val username: StateFlow<String> = preferencesRepo.usernameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val targetGems: StateFlow<Int> = preferencesRepo.targetGemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 500)

    val darkTheme: StateFlow<Boolean?> = preferencesRepo.darkThemeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val appTheme: StateFlow<String> = preferencesRepo.appThemeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "default")

    val shownAchievements: StateFlow<Set<String>> = preferencesRepo.shownAchievementsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val aiMode: StateFlow<String> = preferencesRepo.aiModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "offline")

    val aiProvider: StateFlow<String> = preferencesRepo.aiProviderFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "gemini")

    val geminiApiKey: StateFlow<String> = preferencesRepo.geminiApiKeyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val groqApiKey: StateFlow<String> = preferencesRepo.groqApiKeyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    // All local transactions
    val transactions: StateFlow<List<GemTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Success Notification state
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // Achievement Trigger Popup State (Holds the value of achieved milestone: 100, 500, etc.)
    private val _activeAchievement = MutableStateFlow<Int?>(null)
    val activeAchievement: StateFlow<Int?> = _activeAchievement.asStateFlow()

    // Search and filter state for history
    val searchQuery = MutableStateFlow("")
    val filterSource = MutableStateFlow<String?>(null)
    val filterDateStart = MutableStateFlow<Long?>(null)
    val filterDateEnd = MutableStateFlow<Long?>(null)

    // Filtered transaction list
    val filteredTransactions: StateFlow<List<GemTransaction>> = combine(
        transactions,
        searchQuery,
        filterSource,
        filterDateStart,
        filterDateEnd
    ) { txs, query, source, start, end ->
        txs.filter { tx ->
            val matchQuery = query.isEmpty() || tx.note.contains(query, ignoreCase = true) || tx.source.contains(query, ignoreCase = true)
            val matchSource = source == null || tx.source.lowercase() == source.lowercase()
            val matchStart = start == null || tx.timestamp >= start
            val matchEnd = end == null || tx.timestamp <= end
            matchQuery && matchSource && matchStart && matchEnd
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Automatically checks if total gems crossed achievement boundaries
        viewModelScope.launch {
            combine(transactions, shownAchievements) { txs, achievements ->
                Pair(txs, achievements)
            }.collect { (txs, achievements) ->
                val total = txs.sumOf { it.gems }
                val milestones = listOf(100, 500, 1000, 2000, 5000, 10000)
                for (milestone in milestones) {
                    if (total >= milestone && !achievements.contains(milestone.toString())) {
                        _activeAchievement.value = milestone
                        preferencesRepo.addShownAchievement(milestone.toString())
                        break // Show one achievement at a time
                    }
                }
            }
        }
    }

    fun setScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    val activeProfileId: StateFlow<Int> = preferencesRepo.activeProfileIdFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val profileUsernames: StateFlow<List<String>> = combine(
        (1..5).map { id -> preferencesRepo.getUsernameForProfile(id) }
    ) { names ->
        names.toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), List(5) { "" })

    fun setActiveProfile(profileId: Int) {
        viewModelScope.launch {
            preferencesRepo.saveActiveProfileId(profileId)
        }
    }

    fun saveUsername(name: String) {
        viewModelScope.launch {
            preferencesRepo.saveUsername(activeProfileId.value, name)
        }
    }

    fun saveTargetGems(target: Int) {
        viewModelScope.launch {
            preferencesRepo.saveTargetGems(activeProfileId.value, target)
        }
    }

    fun saveDarkTheme(isDark: Boolean?) {
        viewModelScope.launch {
            preferencesRepo.saveDarkTheme(isDark)
        }
    }

    fun saveAppTheme(theme: String) {
        viewModelScope.launch {
            preferencesRepo.saveAppTheme(theme)
        }
    }

    suspend fun saveAppThemeSync(theme: String) {
        preferencesRepo.saveAppTheme(theme)
    }

    fun saveAiMode(mode: String) {
        viewModelScope.launch {
            preferencesRepo.saveAiMode(mode)
        }
    }

    fun saveAiProvider(provider: String) {
        viewModelScope.launch {
            preferencesRepo.saveAiProvider(provider)
        }
    }

    fun saveGeminiApiKey(key: String) {
        viewModelScope.launch {
            preferencesRepo.saveGeminiApiKey(key)
        }
    }

    fun saveGroqApiKey(key: String) {
        viewModelScope.launch {
            preferencesRepo.saveGroqApiKey(key)
        }
    }

    fun addTransaction(gems: Int, source: String, note: String) {
        viewModelScope.launch {
            repository.insertTransaction(gems, source, note)
            showNotification("Berhasil menambahkan $gems Gems")
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            showNotification("Catatan dihapus")
        }
    }

    private fun showNotification(msg: String) {
        _successMessage.value = msg
    }

    fun clearNotification() {
        _successMessage.value = null
    }

    fun dismissAchievement() {
        _activeAchievement.value = null
    }

    // Reset settings
    fun resetToday() {
        viewModelScope.launch {
            repository.clearTodayOnly()
            showNotification("Berhasil mereset Gems hari ini.")
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.clearAll()
            preferencesRepo.clearPreferences()
            _currentScreen.value = AppScreen.Dashboard
            showNotification("Seluruh data telah di-reset ke 0.")
        }
    }

    // Backup & Restore
    suspend fun generateBackup(): String {
        return repository.generateBackupString()
    }

    suspend fun restoreBackup(backupStr: String): Boolean {
        val success = repository.restoreFromBackupString(backupStr)
        if (success) {
            showNotification("Backup berhasil dipulihkan!")
        } else {
            showNotification("Format backup tidak valid!")
        }
        return success
    }
    
    // Online AI Insight
    private val _onlineAiInsight = MutableStateFlow<String?>(null)
    val onlineAiInsight: StateFlow<String?> = _onlineAiInsight.asStateFlow()

    private val _isFetchingAi = MutableStateFlow(false)
    val isFetchingAi: StateFlow<Boolean> = _isFetchingAi.asStateFlow()

    // Chatbot State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    fun sendChatMessage(message: String) {
        val userMsg = ChatMessage(role = "user", content = message)
        _chatMessages.value = _chatMessages.value + userMsg

        val mode = aiMode.value
        val provider = aiProvider.value
        val geminiKey = geminiApiKey.value
        val groqKey = groqApiKey.value

        if (mode == "offline") {
            _chatMessages.value = _chatMessages.value + ChatMessage(role = "model", content = "Mode AI sedang Offline. Silakan ubah ke Online di menu Settings dan masukkan API Key untuk menggunakan Partner Desa.")
            return
        }

        if (provider == "gemini" && geminiKey.isBlank()) {
            _chatMessages.value = _chatMessages.value + ChatMessage(role = "model", content = "API Key Gemini belum diatur. Silakan isi di menu Settings.")
            return
        }
        if (provider == "groq" && groqKey.isBlank()) {
            _chatMessages.value = _chatMessages.value + ChatMessage(role = "model", content = "API Key Groq belum diatur. Silakan isi di menu Settings.")
            return
        }

        val systemInstruction = "Kamu adalah Partner Desa, asisten ahli untuk game Clash of Clans. Berikan saran yang singkat, padat, dan berguna mengenai upgrade bangunan, pahlawan, mantra, pasukan, atau strategi. Gunakan bahasa Indonesia yang ramah dan bersemangat ala Chief COC. Ingat ini percakapan antara kamu dan Chief."

        viewModelScope.launch {
            _isChatLoading.value = true
            try {
                if (provider == "gemini") {
                    // Gemini format
                    val contents = _chatMessages.value.map { msg ->
                        com.example.data.api.GeminiContent(
                            role = if (msg.role == "user") "user" else "model",
                            parts = listOf(com.example.data.api.GeminiPart(text = msg.content))
                        )
                    }.toMutableList()
                    // Prefix first message with system prompt since free Gemini might not strictly separate system role well without system_instruction block.
                    val firstUserIndex = contents.indexOfFirst { it.role == "user" }
                    if (firstUserIndex != -1) {
                        contents[firstUserIndex] = contents[firstUserIndex].copy(
                            parts = listOf(com.example.data.api.GeminiPart(text = "$systemInstruction\n\n${contents[firstUserIndex].parts.first().text}"))
                        )
                    } else {
                        contents.add(0, com.example.data.api.GeminiContent(role = "user", parts = listOf(com.example.data.api.GeminiPart(text = systemInstruction))))
                    }
                    
                    val req = com.example.data.api.GeminiRequest(contents = contents)
                    val url = "v1beta/models/gemini-1.5-flash:generateContent"
                    val res = com.example.data.api.NetworkModule.aiApiService.generateWithGemini(url, geminiKey, req)
                    val text = res.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    _chatMessages.value = _chatMessages.value + ChatMessage(role = "model", content = text ?: "Gagal mendapatkan respon dari Gemini.")
                } else if (provider == "groq") {
                    val messages = mutableListOf(com.example.data.api.GroqMessage(role = "system", content = systemInstruction))
                    messages.addAll(_chatMessages.value.map { msg ->
                        com.example.data.api.GroqMessage(
                            role = if (msg.role == "user") "user" else "assistant",
                            content = msg.content
                        )
                    })
                    val req = com.example.data.api.GroqRequest(messages = messages)
                    val res = com.example.data.api.NetworkModule.aiApiService.generateWithGroq("Bearer $groqKey", req)
                    val text = res.choices?.firstOrNull()?.message?.content
                    _chatMessages.value = _chatMessages.value + ChatMessage(role = "model", content = text ?: "Gagal mendapatkan respon dari Groq.")
                }
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage(role = "model", content = "Error koneksi: ${e.message}")
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun fetchOnlineAiInsight(target: Int, total: Int, avg: Double) {
        val mode = aiMode.value
        if (mode == "offline") return
        
        val provider = aiProvider.value
        val geminiKey = geminiApiKey.value
        val groqKey = groqApiKey.value

        if (provider == "gemini" && geminiKey.isBlank()) {
            _onlineAiInsight.value = "Gemini API Key belum diatur."
            return
        }
        if (provider == "groq" && groqKey.isBlank()) {
            _onlineAiInsight.value = "Groq API Key belum diatur."
            return
        }

        val prompt = "Saya sedang mengumpulkan gems di game Clash of Clans. Target saya adalah $target gems, dan saat ini saya memiliki $total gems. Rata-rata pendapatan harian saya adalah ${String.format("%.1f", avg)} gems/hari. Berikan kalimat singkat (maks 2 kalimat) motivasi bergaya karakter game atau AI asisten mengenai estimasi waktu saya bisa mencapai target ini."

        viewModelScope.launch {
            _isFetchingAi.value = true
            try {
                if (provider == "gemini") {
                    val req = com.example.data.api.GeminiRequest(
                        contents = listOf(
                            com.example.data.api.GeminiContent(
                                parts = listOf(com.example.data.api.GeminiPart(text = prompt))
                            )
                        )
                    )
                    val url = "v1beta/models/gemini-1.5-flash:generateContent"
                    val res = com.example.data.api.NetworkModule.aiApiService.generateWithGemini(url, geminiKey, req)
                    val text = res.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    _onlineAiInsight.value = text ?: "Gagal mendapatkan insight dari Gemini."
                } else if (provider == "groq") {
                    val req = com.example.data.api.GroqRequest(
                        messages = listOf(
                            com.example.data.api.GroqMessage(content = prompt)
                        )
                    )
                    val res = com.example.data.api.NetworkModule.aiApiService.generateWithGroq("Bearer $groqKey", req)
                    val text = res.choices?.firstOrNull()?.message?.content
                    _onlineAiInsight.value = text ?: "Gagal mendapatkan insight dari Groq."
                }
            } catch (e: Exception) {
                _onlineAiInsight.value = "Error koneksi: ${e.message}"
            } finally {
                _isFetchingAi.value = false
            }
        }
    }

    // Computed Stats values
    val totalGemsToday: StateFlow<Int> = transactions.map { list ->
        val startOfToday = getStartOfToday()
        list.filter { it.timestamp >= startOfToday }.sumOf { it.gems }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalGemsThisWeek: StateFlow<Int> = transactions.map { list ->
        val startOfWeek = getStartOfWeek()
        list.filter { it.timestamp >= startOfWeek }.sumOf { it.gems }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalGemsThisMonth: StateFlow<Int> = transactions.map { list ->
        val startOfMonth = getStartOfMonth()
        list.filter { it.timestamp >= startOfMonth }.sumOf { it.gems }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalGemsOverall: StateFlow<Int> = transactions.map { list ->
        list.sumOf { it.gems }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Helper functions for dates
    private fun getStartOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun getStartOfWeek(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        // Set to first day of week (e.g. Monday or last 7 days)
        cal.add(Calendar.DAY_OF_YEAR, -6)
        return cal.timeInMillis
    }

    private fun getStartOfMonth(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GemsViewModel::class.java)) {
                return GemsViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
