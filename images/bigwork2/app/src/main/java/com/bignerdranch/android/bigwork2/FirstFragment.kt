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
import com.bignerdranch.android.bigwork2.databinding.FragmentFirstBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var userDao: UserDao
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onResume() {
        //当每次可见的时候都进行修改
        super.onResume()
        // 修改 Toolbar 标题
        (activity as? AppCompatActivity)?.supportActionBar?.title = "登录页面"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 修改 Toolbar 标题
//        (activity as? AppCompatActivity)?.supportActionBar?.title = "登录页面"

        userDao = UserDataBase.getDatabase(requireContext()).userDao()

        binding.loginButton.setOnClickListener {
            var username=binding.userName.text.toString()
            val password = binding.password.text.toString()

            lifecycleScope.launch {
                // 将数据库操作放在 withContext(Dispatchers.IO) 代码块中运行，可以确保这些操作不会阻塞主线程
                val user = withContext(Dispatchers.IO) {
                    userDao.getUserByUsername(username)
                }
                if (user?.password==password) {
                    val action = FirstFragmentDirections.actionFirstFragmentToHomePageFragment(username)
                    findNavController().navigate(action)

                } else {
                    Toast.makeText(requireContext(), "账号或密码错误" , Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}