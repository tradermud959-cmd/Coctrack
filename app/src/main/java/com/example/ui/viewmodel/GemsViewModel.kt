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

    val shownAchievements: StateFlow<Set<String>> = preferencesRepo.shownAchievementsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

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

    fun saveUsername(name: String) {
        viewModelScope.launch {
            preferencesRepo.saveUsername(name)
        }
    }

    fun saveTargetGems(target: Int) {
        viewModelScope.launch {
            preferencesRepo.saveTargetGems(target)
        }
    }

    fun saveDarkTheme(isDark: Boolean?) {
        viewModelScope.launch {
            preferencesRepo.saveDarkTheme(isDark)
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
