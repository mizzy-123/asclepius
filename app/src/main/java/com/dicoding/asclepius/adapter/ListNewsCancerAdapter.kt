package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.asclepius.R
import com.dicoding.asclepius.api.response.NewsCancerArticles
import com.dicoding.asclepius.databinding.ItemListNewsCancerBinding

class ListNewsCancerAdapter(private val news: List<NewsCancerArticles>): RecyclerView.Adapter<ListNewsCancerAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: NewsCancerArticles, position: Int)
    }

    internal fun setOnCLickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ListViewHolder(var binding: ItemListNewsCancerBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemListNewsCancerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return news.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val newsCancer = news[position]
        Glide.with(holder.itemView.context)
            .load(newsCancer.urlToImage)
            .apply(RequestOptions().placeholder(R.drawable.ic_place_holder).fitCenter())
            .into(holder.binding.itemPhoto)
        holder.binding.itemName.text = newsCancer.title
        holder.binding.description.text = newsCancer.description

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(newsCancer, position)
        }
    }
}