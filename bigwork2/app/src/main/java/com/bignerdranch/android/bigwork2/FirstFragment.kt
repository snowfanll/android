package com.bignerdranch.android.bigwork2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    findNavController().navigate(R.id.action_FirstFragment_to_homePageFragment)
                } else {
                    Toast.makeText(requireContext(), "账号或密码错误" , Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }
//    private suspend fun validateCredentials(username: String, password: String): Boolean {
//        val user = userDao.getUserByUsername(username)
//        return user?.password == password //在调用一个可能为 null 的对象的方法或访问其属性时，避免出现空指针异常。
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}