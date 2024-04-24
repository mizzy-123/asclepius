package com.dicoding.asclepius.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.databinding.ItemListHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ListHistoryAdapter(private val dataHistory: List<History>) : RecyclerView.Adapter<ListHistoryAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: History, position: Int)
    }

    internal fun setOnCLickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(var binding: ItemListHistoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataHistory.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val history = dataHistory[position]

        val bitmap = history.foto?.let { BitmapFactory.decodeByteArray(history.foto, 0, it.size) }
        Glide.with(holder.itemView.context)
            .load(bitmap)
            .into(holder.binding.imgCancer)
        holder.binding.tvCancer.text = history.name
        holder.binding.tvScore.text = history.score

        val formateDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val dateTimeString = history.createdAt?.let { formateDate.format(it) }
        holder.binding.tvTanggal.text = dateTimeString

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(history, position)
        }
    }
}