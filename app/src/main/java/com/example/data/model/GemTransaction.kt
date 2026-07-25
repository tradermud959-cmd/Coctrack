package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gem_transactions")
data class GemTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileId: Int = 1,
    val gems: Int,
    val source: String, // Tree, Rock, Grass, Gem Box, Sell Book, Sell Spell, Mission, Others (localized in UI)
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
