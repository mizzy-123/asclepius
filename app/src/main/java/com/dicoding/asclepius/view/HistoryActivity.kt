package com.dicoding.asclepius.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.ListHistoryAdapter
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.databinding.DialogDetailHistoryBinding
import com.dicoding.asclepius.helper.ViewModelFactory
import com.dicoding.asclepius.model.HistoryViewModel

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var listHistoryAdapter: ListHistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyViewModel = obtainViewModel(this@HistoryActivity)

        historyViewModel.getAllHistory().observe(this){ listHistory ->
            showRecycleListView(listHistory)
        }

        binding.toolBar.setNavigationOnClickListener {
            finish()
        }

    }

    private fun showRecycleListView(listHistory: List<History>){
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        listHistoryAdapter = ListHistoryAdapter(listHistory)
        binding.rvHistory.adapter = listHistoryAdapter
        listHistoryAdapter.setOnCLickCallback(object : ListHistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: History, position: Int) {
                val bitmap = data.foto?.let { BitmapFactory.decodeByteArray(data.foto, 0, it.size) }
                if (bitmap != null) {
                    showDetailHistory(bitmap, data.name!!, data.score!!)
                }
            }

        })

    }

    private fun showDetailHistory(foto: Bitmap, name: String, score: String){
        val inflater = DialogDetailHistoryBinding.inflate(layoutInflater)

        Glide.with(inflater.root)
            .load(foto)
            .into(inflater.previewImageView)

        inflater.itemName.text = name
        inflater.itemScore.text = score
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("History Detail")
            .setView(inflater.root)
            .create()
        alertDialog.show()
    }

    private fun obtainViewModel(activity: AppCompatActivity): HistoryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(HistoryViewModel::class.java)
    }
}