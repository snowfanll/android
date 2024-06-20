package com.bignerdranch.android.bigwork2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {

    @Insert
    fun insertHistory(history: History)

    @Query("SELECT * FROM history_records WHERE sendText != '' AND username = :username ORDER BY timestamp DESC LIMIT 5")
    fun getLatestHistoryByUserLimit5(username: String): List<History>

    @Query("SELECT * FROM history_records WHERE sendText != '' AND username = :username ORDER BY timestamp DESC")
    fun getHistoryByUser(username: String): List<History>

    // 根据情感来进行搜索
    @Query("SELECT * FROM history_records WHERE username = :username AND responseText = :text ORDER BY timestamp DESC")
    fun getHistoryByUserAndDateOrEmotion(username: String,text: String): List<History>

    // 删除所有历史记录
    @Query("DELETE FROM history_records WHERE username=:username")
    fun deleteAllHistory(username: String)

    // 删除单个历史记录
    @Delete
    fun deleteHistory(history: History)
}
