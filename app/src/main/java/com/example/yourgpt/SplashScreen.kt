package com.example.yourgpt

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        window?.statusBarColor= Color.WHITE

        // Delay the transition to the main activity
        Handler().postDelayed({
            val intent = Intent(this, FirstActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}
