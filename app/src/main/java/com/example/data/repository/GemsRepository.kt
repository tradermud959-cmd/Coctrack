package com.example.data.repository

import android.util.Base64
import com.example.data.db.GemTransactionDao
import com.example.data.model.BackupData
import com.example.data.model.BackupTransaction
import com.example.data.model.GemTransaction
import com.example.data.preferences.UserPreferencesRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import java.util.Calendar

class GemsRepository(
    private val gemDao: GemTransactionDao,
    private val preferencesRepo: UserPreferencesRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val allTransactions: Flow<List<GemTransaction>> = preferencesRepo.activeProfileIdFlow.flatMapLatest { profileId ->
        gemDao.getAllTransactions(profileId)
    }

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val backupAdapter = moshi.adapter(BackupData::class.java)

    suspend fun insertTransaction(gems: Int, source: String, note: String, timestamp: Long = System.currentTimeMillis()) {
        val profileId = preferencesRepo.activeProfileIdFlow.first()
        val transaction = GemTransaction(
            profileId = profileId,
            gems = gems,
            source = source,
            note = note,
            timestamp = timestamp
        )
        gemDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Int) {
        gemDao.deleteTransaction(id)
    }

    suspend fun clearAll() {
        val profileId = preferencesRepo.activeProfileIdFlow.first()
        gemDao.clearAllTransactions(profileId)
        // Note: we don't clear preferences completely anymore, or maybe we just reset for this profile?
        // Wait, clearPreferences clears everything! We should probably not call it here, or just reset username/target.
        preferencesRepo.saveUsername(profileId, "")
        preferencesRepo.saveTargetGems(profileId, 500)
    }

    suspend fun clearTodayOnly() {
        val profileId = preferencesRepo.activeProfileIdFlow.first()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis
        gemDao.clearTransactionsFromToday(profileId, startOfToday)
    }

    suspend fun generateBackupString(): String {
        val profileId = preferencesRepo.activeProfileIdFlow.first()
        val username = preferencesRepo.usernameFlow.first()
        val targetGems = preferencesRepo.targetGemsFlow.first()
        val txs = gemDao.getAllTransactions(profileId).first()

        val backupTxs = txs.map {
            BackupTransaction(
                gems = it.gems,
                source = it.source,
                note = it.note,
                timestamp = it.timestamp
            )
        }

        val backupData = BackupData(
            username = username,
            targetGems = targetGems,
            transactions = backupTxs
        )

        val json = backupAdapter.toJson(backupData)
        return Base64.encodeToString(json.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    suspend fun restoreFromBackupString(backupStr: String): Boolean {
        return try {
            val profileId = preferencesRepo.activeProfileIdFlow.first()
            val decodedStr = backupStr.trim()
            val jsonBytes = Base64.decode(decodedStr, Base64.NO_WRAP)
            val json = String(jsonBytes, Charsets.UTF_8)
            val backupData = backupAdapter.fromJson(json) ?: return false

            preferencesRepo.saveUsername(profileId, backupData.username)
            preferencesRepo.saveTargetGems(profileId, backupData.targetGems)

            gemDao.clearAllTransactions(profileId)

            for (tx in backupData.transactions) {
                gemDao.insertTransaction(
                    GemTransaction(
                        profileId = profileId,
                        gems = tx.gems,
                        source = tx.source,
                        note = tx.note,
                        timestamp = tx.timestamp
                    )
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
