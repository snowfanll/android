package com.bignerdranch.android.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bignerdranch.android.myapplication.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null  //?代表变量可以为空 => _binding可以为空 初始赋值为空

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!   //!!确定不为空

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)  //实例化 //_binding第一个页面的实例
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.randomButton.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment);
            val showCountTextView=view.findViewById<TextView>(R.id.textview_first);
            val currentCount=showCountTextView.text.toString().toInt();
            val action=FirstFragmentDirections.actionFirstFragmentToSecondFragment(currentCount);
            findNavController().navigate(action);

        }

        view.findViewById<Button>(R.id.toast_button).setOnClickListener {
            var mytoast=Toast.makeText(context,"Hello Toast!",Toast.LENGTH_LONG);
            mytoast.show();
        }

        view.findViewById<Button>(R.id.count_button).setOnClickListener{
            countMe(view);
        }

    }
    private fun countMe(view: View){
        val showCountTextView=view.findViewById<TextView>(R.id.textview_first)
        val countString=showCountTextView.text.toString();
        var count=countString.toInt();
        count++;
        showCountTextView.text=count.toString();
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}