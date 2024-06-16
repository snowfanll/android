package com.bignerdranch.android.bigwork2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.bigwork2.databinding.FragmentHistoryRecordBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHistoryRecordBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onResume() {
        //当每次可见的时候都进行修改
        super.onResume()
        // 修改 Toolbar 标题
        (activity as? AppCompatActivity)?.supportActionBar?.title = "历史记录"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 得到传入的参数userName
        val userName=arguments?.let { HomePageFragmentArgs.fromBundle(it).userName }.toString()

//        Toast.makeText(requireContext(),userName,Toast.LENGTH_SHORT).show()

        historyDao = UserDataBase.getDatabase(requireContext()).historyDao()
        historyAdapter = HistoryAdapter(emptyList())

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        loadAllHistory(userName)


    }

    private fun loadAllHistory(userName:String) {
        lifecycleScope.launch {
            val historyList = withContext(Dispatchers.IO) {
                historyDao.getHistoryByUser(userName)
            }
            historyAdapter.updateHistory(historyList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}