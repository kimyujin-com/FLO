package com.example.flo.ui.main.locker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.ui.signin.LoginActivity
import com.example.flo.ui.main.MainActivity
import com.example.flo.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {
    lateinit var binding: FragmentLockerBinding
    private val information = arrayListOf("저장한곡", "음악파일", "저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerAdapter
        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp){
                tab, position ->
            tab.text = information[position]
        }.attach()

        binding.lockerLoginTv.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }



//        binding.lockerSelectAllTv.setOnClickListener {
//
//        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        initViews()
    }

    private fun initViews() {
        val jwt: String? = getJwt()

        if (jwt == null){
            binding.lockerLoginTv.text = "로그인"

            binding.lockerLoginTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
        else{
            binding.lockerLoginTv.text = "로그아웃"

            binding.lockerLoginTv.setOnClickListener {
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    private fun getJwt(): String? {
        val spf = activity?.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)
        return spf?.getString("jwt", null)
    }

    private fun logout() {
        val spf = activity?.getSharedPreferences("auth2" , AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()

        editor.remove("jwt")
        editor.apply()
    }
}