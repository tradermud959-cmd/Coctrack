package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BackupTransaction(
    val gems: Int,
    val source: String,
    val note: String,
    val timestamp: Long
)

@JsonClass(generateAdapter = true)
data class BackupData(
    val username: String,
    val targetGems: Int,
    val transactions: List<BackupTransaction>
)
