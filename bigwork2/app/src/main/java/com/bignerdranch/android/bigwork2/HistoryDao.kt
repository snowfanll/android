package com.bignerdranch.android.bigwork2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert
    fun insertHistory(history: History)

    @Query("SELECT * FROM history_entries ORDER BY id DESC")
    fun getAllHistory(): Flow<List<History>>
}
