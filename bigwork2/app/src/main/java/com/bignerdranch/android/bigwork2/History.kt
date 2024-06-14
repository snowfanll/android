package com.bignerdranch.android.bigwork2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_records")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val sendText: String,
    val responseText: String,
    val timestamp: Long = System.currentTimeMillis()
)
