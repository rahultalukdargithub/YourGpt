package com.example.yourgpt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        window?.statusBarColor= resources.getColor(R.color.appbg)

        val card =findViewById<CardView>(R.id.cardView)
        val card2 =findViewById<CardView>(R.id.cardView2)

        card.setOnClickListener {
            val gpt = Intent(this,MainActivity::class.java)
            startActivity(gpt)
        }
        card2.setOnClickListener {
            val ig = Intent(this,ImageGenerater::class.java)
            startActivity(ig)
        }
    }
}