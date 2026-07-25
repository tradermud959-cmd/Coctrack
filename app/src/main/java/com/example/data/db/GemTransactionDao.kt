package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.GemTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GemTransactionDao {
    @Query("SELECT * FROM gem_transactions WHERE profileId = :profileId ORDER BY timestamp DESC")
    fun getAllTransactions(profileId: Int): Flow<List<GemTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: GemTransaction)

    @Query("DELETE FROM gem_transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Int)

    @Query("DELETE FROM gem_transactions WHERE profileId = :profileId")
    suspend fun clearAllTransactions(profileId: Int)

    @Query("DELETE FROM gem_transactions WHERE profileId = :profileId AND timestamp >= :startOfDay")
    suspend fun clearTransactionsFromToday(profileId: Int, startOfDay: Long)
}
