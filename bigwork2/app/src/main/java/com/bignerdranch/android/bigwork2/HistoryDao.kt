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

    // 根据用户和指定日期获取历史记录
    @Query("SELECT * FROM history_records WHERE username = :username AND (strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch') = :date OR responseText = :responseText)")
    fun getHistoryByUserAndDate(username: String, date: String,responseText:String): List<History>

    // 删除所有历史记录
    @Query("DELETE FROM history_records")
    fun deleteAllHistory()

    // 删除单个历史记录
    @Delete
    fun deleteHistory(history: History)
}
