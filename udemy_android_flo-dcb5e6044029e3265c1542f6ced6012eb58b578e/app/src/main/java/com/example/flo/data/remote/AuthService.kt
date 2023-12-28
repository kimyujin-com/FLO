package com.example.flo.data.remote

import android.util.Log
import com.example.flo.ui.signin.AutoLoginView
import com.example.flo.ui.signin.LoginView
import com.example.flo.ui.signup.SignUpView
import com.example.flo.data.entities.User
import com.example.flo.utils.getRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private lateinit var signUpView: SignUpView
    private lateinit var loginView: LoginView
    private lateinit var autoLoginView: AutoLoginView

    fun setSignUpView(signUpView: SignUpView) {
        this.signUpView = signUpView
    }

    fun setLoginView(loginView: LoginView) {
        this.loginView = loginView
    }

    fun setAutoLoginView(autoLoginView: AutoLoginView) {
        this.autoLoginView = autoLoginView
    }

    fun signUp(user: User) {

        val signUpService = getRetrofit().create(AuthRetrofitInterface::class.java)

        signUpService.signUp(user).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    val signUpResponse: AuthResponse = response.body()!!

                    Log.d("SIGNUP-RESPONSE", signUpResponse.toString())

                    when (val code = signUpResponse.code) {
                        1000 -> signUpView.onSignUpSuccess()
                        2016, 2017 -> {
                            signUpView.onSignUpFailure()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                //실패처리
            }
        })
    }


    fun login(user: User) {
        val loginService = getRetrofit().create(AuthRetrofitInterface::class.java)


        loginService.login(user).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    val loginResponse: AuthResponse = response.body()!!

                    when (val code = loginResponse.code) {
                        1000 -> loginView.onLoginSuccess(code,loginResponse.result!! )
                        else -> loginView.onLoginFailure()
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                //실패처리
            }
        })
    }

    fun autoLogin(jwt:String?) {
        val autoLoginService = getRetrofit().create(AuthRetrofitInterface::class.java)

        autoLoginService.getData(jwt).enqueue(object : Callback<AutoLoginResponse> {
            override fun onResponse(call: Call<AutoLoginResponse>, response: Response<AutoLoginResponse>) {
                if (response.isSuccessful && response.code() == 200) {
                    val autoLoginResponse: AutoLoginResponse = response.body()!!

                    when (val code = autoLoginResponse.code) {
                        1000 -> autoLoginView.onAutoLoginSuccess(code)
                        2002, 2001 -> {
                            Log.d("TAG", autoLoginResponse.message)
                            autoLoginView.onAutoLoginFailure()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AutoLoginResponse>, t: Throwable) {
                //실패처리
            }
        })
    }
}