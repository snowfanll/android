package com.bignerdranch.android.bigwork2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.bigwork2.databinding.FragmentHomepageBinding
import com.example.tflitebigwork.TextAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.label.Category

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomePageFragment : Fragment() , TextAnalyzer.TextResultsListener{

    private var _binding: FragmentHomepageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var userDao: UserDao
    private lateinit var historyDao: HistoryDao
    private lateinit var historyAdapter: HistoryAdapter

    private lateinit var userName:String
    private lateinit var sendText:String

    private lateinit var textClassifier: TextAnalyzer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomepageBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root

    }

    override fun onResume() {
        //当每次可见的时候都进行修改
        super.onResume()
        setHasOptionsMenu(true)
        // 修改 Toolbar 标题
        (activity as? AppCompatActivity)?.supportActionBar?.title = "主页"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化 TextClassificationHelper
        textClassifier = TextAnalyzer(requireContext(), this)

        // 初始化 historyDao
        historyDao = UserDataBase.getDatabase(requireContext()).historyDao()
        // 初始化 historyAdapter，初始数据为空
        historyAdapter = HistoryAdapter(emptyList()) { history ->
            showDeleteSingleRecordDialog(history)
        }
        // 配置 RecyclerView
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())  // 设置布局管理器
            adapter = historyAdapter  // 设置适配器
        }

        // 得到传入的参数userName
        userName=arguments?.let { HomePageFragmentArgs.fromBundle(it).userName }.toString()

        // 加载历史记录
        loadHistory(userName)

        // 发送按钮点击事件
        binding.sendButton.setOnClickListener {
            sendText = binding.sendTextView.text.toString()  // 获取输入的发送信息
//            val analysisText="分析"
//            val responseText = "回复"  // 自动回复信息的内容

            if (sendText.isNotEmpty()) {
                analyzeSentimentAndSaveHistory(sendText)
//                saveHistory(userName,sendText,analysisText,responseText)  // 保存历史记录
                binding.sendTextView.text.clear()  // 清空输入框
            }
        }

    }

    private fun analyzeSentimentAndSaveHistory(sendText: String) {
        textClassifier.classify(sendText)
    }


    private fun loadHistory(username:String) {
        lifecycleScope.launch {
            val historyList = withContext(Dispatchers.IO) {
                historyDao.getLatestHistoryByUserLimit5(username)  // 获取最新的5条历史记录
            }
            historyAdapter.updateHistory(historyList.reversed()) // 反转列表
        }
    }

    override fun onResult(results: List<Category>, inferenceTime: Long) {
        var star_level = 0.0
        var analysisText=""
        var responseText=""
        var i=0
        for (str in results){
            analysisText += str.label + ":" + str.score
            if(i<results.size-1)
                analysisText+="\n"
            if (str.label == "positive"){
                star_level += str.score
            }else{
                star_level -= str.score
            }
            i++
        }
        System.out.println(star_level)
        if(star_level < -0.1){
            responseText="消极"
        }else if(star_level < 0.2){
            responseText="中立"
        }else{
            responseText="积极"
        }
        saveHistory(userName, sendText, analysisText, responseText)
    }

    private fun saveHistory(username: String, sendText: String,analysisText:String, responseText: String) {
        val newHistory = History(
            username = username,  // 用户名
            sendText = sendText,  // 发送的文本
            analysisText=analysisText,
            responseText = responseText,  // 回复的文本
            timestamp = System.currentTimeMillis()  // 当前时间戳
        )

        lifecycleScope.launch(Dispatchers.IO) {
            historyDao.insertHistory(newHistory)  // 插入新的历史记录
            loadHistory(username)  // 重新加载历史记录，更新显示
        }
    }


    // 删除单个历史记录  长按删除
    private fun showDeleteSingleRecordDialog(history: History) {
        AlertDialog.Builder(requireContext())
            .setTitle("确认删除")
            .setMessage("您确定要删除这条历史记录吗？")
            .setPositiveButton("是") { dialog, which ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        historyDao.deleteHistory(history)
                    }
                    loadHistory(userName)  // 显示最新的5条
                    Toast.makeText(context, "历史记录已删除", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("否", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_history, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.history_record -> {
                    // 如果当前在 SecondFragment，直接查看历史记录
//                    Toast.makeText(requireContext(), "查看历史记录", Toast.LENGTH_SHORT).show()
                    // 跳转历史记录页面
                    val action=HomePageFragmentDirections.actionHomePageFragmentToHistoryRecordFragment(userName)
                    findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onError(error: String) {
        Toast.makeText(requireContext(), "情感分析错误: $error", Toast.LENGTH_SHORT).show()
    }

}