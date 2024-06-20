package com.iftah.herbflora.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iftah.herbflora.article.ArticleActivity
import com.iftah.herbflora.databinding.LayoutItemArticleBinding
import com.iftah.herbflora.model.Article

class ArticlePagingAdapter :
    PagingDataAdapter<Article, ArticlePagingAdapter.ArticleViewHolder>(DIFF_CALLBACK) {

    class ArticleViewHolder(private val binding: LayoutItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            with(binding) {

                name.text = article.name
                keywords.text = article.keywords


                Glide.with(itemView.context).load(article.photoUrl).into(photo)
            }
        }
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
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
    ): ArticleViewHolder {
        val binding =
            LayoutItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ArticleViewHolder(binding)
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

