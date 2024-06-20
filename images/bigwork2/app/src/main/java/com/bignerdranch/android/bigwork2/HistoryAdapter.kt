package com.bignerdranch.android.bigwork2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(private var historyList: List<History>,private val onLongClick: (History) -> Unit) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

        // ViewHolder类持有每个列表项的视图
    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sendText: TextView = itemView.findViewById(R.id.sendText)  //发送文本
        val analysisText:TextView=itemView.findViewById(R.id.analyseText)  //分析文本
        val responseText: TextView = itemView.findViewById(R.id.responseText) //回复文本
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)  //时间戳
    }

    // 创建ViewHolder实例并加载XML布局
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_record_item, parent, false)
        return HistoryViewHolder(view)
    }

    // 将数据绑定到ViewHolder
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.sendText.text = history.sendText
        holder.analysisText.text=history.analysisText
        holder.responseText.text = history.responseText

        // 将时间戳格式化为 "年/月/日 时:分:秒"
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val formattedDate = sdf.format(Date(history.timestamp))
        holder.timestamp.text = formattedDate

        // 设置长按事件监听器
        holder.itemView.setOnLongClickListener {
            onLongClick(history)
            true
        }
    }

    // 返回列表项的数量
    override fun getItemCount() = historyList.size

    // 更新历史记录并通知适配器数据已更改
    fun updateHistory(newHistoryList: List<History>) {
        historyList = newHistoryList
        notifyDataSetChanged()
    }
}
