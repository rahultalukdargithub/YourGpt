package com.example.yourgpt

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class ImageGenerater : AppCompatActivity() {
    private lateinit var inputText: EditText
    private lateinit var generateBtn: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var downloadBtn: ImageButton
    private var imageUrl: String? = null


    companion object {
        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    }

    private val client = OkHttpClient.Builder().readTimeout(60 , TimeUnit.SECONDS).build()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_generater)
        window?.statusBarColor= resources.getColor(R.color.appbg2)
        inputText = findViewById(R.id.input_text)
        generateBtn = findViewById(R.id.generate_btn)
        progressBar = findViewById(R.id.progress_bar)
        imageView = findViewById(R.id.image_view)
        downloadBtn = findViewById(R.id.downbtn)

        generateBtn.setOnClickListener {
            val text = inputText.text.toString().trim()
            if (text.isEmpty()) {
                inputText.error = "Text can't be empty"
                return@setOnClickListener
            }
            callAPI(text)
        }

        downloadBtn.setOnClickListener {
            imageUrl?.let { url ->
                val text = inputText.text.toString().trim()
                downloadImage(url,text)
            } ?: run {
                Toast.makeText(applicationContext, "Image URL not available", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun callAPI(text: String) {
        setInProgress(true)
        val jsonBody = JSONObject()
        try {
            jsonBody.put("prompt", text)
            jsonBody.put("size", "256x256")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val requestBody = jsonBody.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/images/generations")
            .header("Authorization", "Bearer YOUR_API_KEY")
            .post(requestBody)
            .build()
//        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
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread(Runnable(){
                    run(){
                        Toast.makeText(applicationContext, "Failed to generate image", Toast.LENGTH_LONG).show()
                    }

                })
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonObject = JSONObject(response.body!!.string())
                    imageUrl = jsonObject.getJSONArray("data").getJSONObject(0).getString("url")
                    imageUrl?.let { loadImage(it) }
                    setInProgress(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        })

    }

    private fun setInProgress(inProgress: Boolean) {


        runOnUiThread(Runnable(){
            run(){
                if (inProgress) {
                    progressBar.visibility = View.VISIBLE
                    generateBtn.visibility = View.GONE
                    downloadBtn.visibility=View.GONE
                } else {
                    progressBar.visibility = View.GONE
                    generateBtn.visibility = View.VISIBLE
                    downloadBtn.visibility=View.VISIBLE
                }
            }

        })
    }

    private fun loadImage(url: String) {

        runOnUiThread(Runnable(){
            run(){
                Picasso.get().load(url).into(imageView)
            }

        })
    }


    private fun downloadImage(url: String,text: String) {

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val uri = Uri.parse(url)

        val request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverRoaming(false)
            .setTitle(text)
            .setMimeType("image/jpeg")
            .setDescription("Image Download in progress")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        // Set the destination path for the downloaded file
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, File.separator+text+".jpg")

        downloadManager.enqueue(request)
    }

}
