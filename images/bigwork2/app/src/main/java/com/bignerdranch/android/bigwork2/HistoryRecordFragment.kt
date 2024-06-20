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
import com.bignerdranch.android.bigwork2.databinding.FragmentHistoryRecordBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HistoryRecordFragment : Fragment() {

    private var _binding: FragmentHistoryRecordBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var historyDao: HistoryDao
    private lateinit var historyAdapter: HistoryAdapter

    private lateinit var userName: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHistoryRecordBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)  //
        return binding.root

    }

    override fun onResume() {
        //当每次可见的时候都进行修改
        super.onResume()
        setHasOptionsMenu(true)
        // 修改 Toolbar 标题
        (activity as? AppCompatActivity)?.supportActionBar?.title = "历史记录"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 得到传入的参数userName
        userName=arguments?.let { HomePageFragmentArgs.fromBundle(it).userName }.toString()

//        Toast.makeText(requireContext(),userName,Toast.LENGTH_SHORT).show()

        historyDao = UserDataBase.getDatabase(requireContext()).historyDao()

        historyAdapter = HistoryAdapter(emptyList()) { history ->
            showDeleteSingleRecordDialog(history)
        }

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        loadAllHistory(userName)

        //根据情感来查询相关历史记录
        binding.searchButton.setOnClickListener{
            val text=binding.sendTextView.text.toString()
            searchHistory(text) //搜索历史记录
        }

    }

    private fun loadAllHistory(userName:String) {
        lifecycleScope.launch {
            val historyList = withContext(Dispatchers.IO) {
                historyDao.getHistoryByUser(userName)
            }
            historyAdapter.updateHistory(historyList)
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
                    loadAllHistory(userName)
                    Toast.makeText(context, "历史记录已删除", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("否", null)
            .show()
    }

    //根据情感查询
    private fun searchHistory(text: String) {
        lifecycleScope.launch {
            val historyList = withContext(Dispatchers.IO) {
                historyDao.getHistoryByUserAndDateOrEmotion(userName,text)
            }
            historyAdapter.updateHistory(historyList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_delete_history, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                // 处理垃圾桶图标点击事件  删除所有历史记录
                // 弹出确认对话框
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 是否要删除所有历史记录的对话框
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("确认删除")
            .setMessage("您确定要删除所有历史记录吗？")
            .setPositiveButton("是") { dialog, which ->
                // 删除所有历史记录
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        historyDao.deleteAllHistory(userName)
                    }
                    // 刷新列表
                    loadAllHistory(userName)
                    Toast.makeText(context, "所有历史记录已删除", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("否", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}