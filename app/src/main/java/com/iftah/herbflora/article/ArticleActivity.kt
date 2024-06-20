package com.iftah.herbflora.article

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.iftah.herbflora.R
import com.iftah.herbflora.databinding.ActivityArticleBinding
import com.iftah.herbflora.model.Article

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding
    private lateinit var articleViewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        articleViewModel = ViewModelProvider(this)[ArticleViewModel::class.java]

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val receivedIntent = intent.extras
        val nameArticle = receivedIntent?.getString("Name Article").toString()

        articleViewModel.getDataArticle(nameArticle)

        articleViewModel.article.observe(this){
            getDetailArticle(it)
        }
        articleViewModel.message.observe(this) { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        articleViewModel.isLoading.observe(this){ showLoading(it) }
    }

    private fun getDetailArticle(article: Article){
        binding.name.text = article.name
        binding.desc.text = article.desc
        binding.benefits.text = article.benefits
        binding.usage.text = article.usage
        binding.keywords.text = article.keywords
        binding.plantCare.text = article.plantCare
        supportActionBar?.title = article.name
        Glide.with(this).load(article.photoUrl).into(binding.image)
        binding.descText.visibility = View.VISIBLE
        binding.benefitsText.visibility = View.VISIBLE
        binding.plantCareText.visibility = View.VISIBLE
        binding.usageText.visibility = View.VISIBLE
        binding.photoCard.visibility = View.VISIBLE
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}