package com.iftah.herbflora.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.iftah.herbflora.model.Article
import com.iftah.herbflora.paging.RecentPagingSource

class Repository {

    fun getPosts() : LiveData<PagingData<Article>>{
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                RecentPagingSource()
            }
        ).liveData
    }
}