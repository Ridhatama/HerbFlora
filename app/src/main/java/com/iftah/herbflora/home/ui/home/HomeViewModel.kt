package com.iftah.herbflora.home.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.iftah.herbflora.home.ui.articleList.ArticleViewModel
import com.iftah.herbflora.model.Article
import com.iftah.herbflora.repository.Repository

class HomeViewModel(repository: Repository) : ViewModel() {

    val article: LiveData<PagingData<Article>> =
        repository.getPosts().cachedIn(viewModelScope)
}

class HomeViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}