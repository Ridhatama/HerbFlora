package com.iftah.herbflora.home.ui.articleList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.iftah.herbflora.model.Article
import com.iftah.herbflora.repository.Repository

class ArticleViewModel(repository: Repository): ViewModel() {

    val article: LiveData<PagingData<Article>> =
        repository.getPosts().cachedIn(viewModelScope)
}

class ArticleViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}