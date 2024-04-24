package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.ListNewsCancerAdapter
import com.dicoding.asclepius.api.response.NewsCancerArticles
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.dicoding.asclepius.helper.ViewModelFactory
import com.dicoding.asclepius.model.NewsCancerViewModel

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsCancerViewModel: NewsCancerViewModel
    private lateinit var listNewsCancerAdapter: ListNewsCancerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsCancerViewModel = obtainViewModel(this@NewsActivity)

        newsCancerViewModel.newsCancer(this@NewsActivity)

        newsCancerViewModel.isLoading.observe(this){

            if (it){
                binding.rvNews.visibility = View.INVISIBLE
                binding.loading.visibility = View.VISIBLE
            } else {
                newsCancerViewModel.newsCancer.observe(this){ newsCancer ->
                    showRecyclerCardView(newsCancer.articles)
                }
                binding.rvNews.visibility = View.VISIBLE
                binding.loading.visibility = View.INVISIBLE
            }

        }
    }

    private fun showRecyclerCardView(listItem: List<NewsCancerArticles>){
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        listNewsCancerAdapter = ListNewsCancerAdapter(listItem)
        binding.rvNews.adapter = listNewsCancerAdapter

        listNewsCancerAdapter.setOnCLickCallback(object : ListNewsCancerAdapter.OnItemClickCallback {
            override fun onItemClicked(data: NewsCancerArticles, position: Int) {
                val intent = Intent(this@NewsActivity, DetailNewsActivity::class.java)
                intent.putExtra(DetailNewsActivity.URL_NEWS_ARTICLE, data.url)
                startActivity(intent)
            }

        })
    }

    private fun obtainViewModel(activity: AppCompatActivity): NewsCancerViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(NewsCancerViewModel::class.java)
    }
}