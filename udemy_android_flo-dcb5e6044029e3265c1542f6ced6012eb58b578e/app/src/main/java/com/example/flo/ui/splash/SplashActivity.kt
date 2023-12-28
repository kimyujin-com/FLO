package com.example.flo.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.ui.signin.AutoLoginView
import com.example.flo.ui.signin.LoginActivity
import com.example.flo.ui.main.MainActivity
import com.example.flo.data.remote.AuthService
import com.example.flo.databinding.ActivitySplashBinding

class SplashActivity  : AppCompatActivity() , AutoLoginView {
    lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        autoLogin()
    }

    private fun autoLogin() {
        val authService = AuthService()
        authService.setAutoLoginView(this)
        var jwt = getJwt()
        if(jwt == null)
            Log.d("TAG", "JWT가 비어있음")
        authService.autoLogin(jwt)
    }

    override fun onAutoLoginSuccess(code : Int) {
        when(code) {
            1000 -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
        }
    }

    override fun onAutoLoginFailure() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getJwt(): String? {
        val spf = getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)
        return spf.getString("jwt", null)
    }
}