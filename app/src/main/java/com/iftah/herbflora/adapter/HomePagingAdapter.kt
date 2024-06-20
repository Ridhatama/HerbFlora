package com.iftah.herbflora.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iftah.herbflora.article.ArticleActivity
import com.iftah.herbflora.databinding.LayoutItemHomeBinding
import com.iftah.herbflora.model.Article

class HomePagingAdapter :
    PagingDataAdapter<Article, HomePagingAdapter.HomeViewHolder>(DIFF_CALLBACK) {

    class HomeViewHolder(private val binding: LayoutItemHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            with(binding) {

                name.text = article.name
                Glide.with(itemView.context).load(article.photoUrl).into(image)
            }
        }
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val data = getItem(position)

        if (data != null) {
            holder.bind(data)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ArticleActivity::class.java)
            intent.putExtra("Name Article", data?.name)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HomeViewHolder {
        val binding =
            LayoutItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return HomeViewHolder(binding)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}