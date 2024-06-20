package com.bignerdranch.android.bigwork2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_records")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val username: String,  // 姓名
    val sendText: String,  //发送的消息
    val analysisText:String,   //分析百分比
    val responseText: String,  // 根据百分比得到的最后结果
    val timestamp: Long = System.currentTimeMillis()
)
