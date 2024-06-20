package com.iftah.herbflora.splashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.iftah.herbflora.R
import com.iftah.herbflora.login.LoginActivity
import com.iftah.herbflora.onBoarding.OnBoardingActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        splashScreen.setKeepOnScreenCondition { true }
        startSomeNextActivity()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        finish()
    }

    private fun startSomeNextActivity() {
        val intent = Intent(this, OnBoardingActivity::class.java)
        startActivity(intent)
    }
}