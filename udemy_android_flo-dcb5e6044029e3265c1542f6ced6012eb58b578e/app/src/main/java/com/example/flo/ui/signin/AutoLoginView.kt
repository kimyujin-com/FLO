package com.example.flo.ui.signin

interface AutoLoginView {
    fun onAutoLoginSuccess(code : Int)
    fun onAutoLoginFailure()
}