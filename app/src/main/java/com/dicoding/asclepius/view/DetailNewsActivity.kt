package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityDetailNewsBinding

class DetailNewsActivity : AppCompatActivity() {

    companion object {
        const val URL_NEWS_ARTICLE = "url_news_article"
    }
    private lateinit var binding: ActivityDetailNewsBinding
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras

        if (bundle != null){
            val url: String = bundle.getString(URL_NEWS_ARTICLE).toString()
            binding.webView.webViewClient = WebViewClient()
            binding.webView.loadUrl(url)

            val webSettings = binding.webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.databaseEnabled = true
            webSettings.domStorageEnabled = true
        }
    }
}