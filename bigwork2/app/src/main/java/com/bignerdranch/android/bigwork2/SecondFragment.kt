package com.bignerdranch.android.bigwork2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.bigwork2.databinding.FragmentSecondBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    private lateinit var userDao: UserDao

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDao = UserDataBase.getDatabase(requireContext()).userDao()

        binding.registerButton.setOnClickListener {
            val username = binding.newUsernameEditText.text.toString()
            val password = binding.newPasswordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                registerUser(username, password)
            } else {
                Toast.makeText(requireContext(), "Please enter a username and password", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun registerUser(username: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser != null) {
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "用户已存在", Toast.LENGTH_SHORT).show()
                }
            } else {
                val newUser = User(username = username, password = password)
                userDao.insertUser(newUser)
                launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "注册成功", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}