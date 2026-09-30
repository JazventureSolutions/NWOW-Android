package com.example.nwow.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.example.nwow.R
import com.example.nwow.ui.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(
                Intent(
                    this@SplashActivity,
                    LoginActivity::class.java
                )
            )
            finish()
        }, 3000)
    }
}