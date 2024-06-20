package com.iftah.herbflora.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.iftah.herbflora.model.Article
import kotlinx.coroutines.tasks.await

class RecentPagingSource : PagingSource<DocumentSnapshot, Article>() {


    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Article> {
        return try {
            val page = params.key
            val pageSize = params.loadSize

            val firestore = FirebaseFirestore.getInstance()
            val collectionRef = firestore.collection("article")

            val query = collectionRef.orderBy("name", Query.Direction.DESCENDING).limit(pageSize.toLong())

            val currentPageQuery = if (page == null){
                query
            } else {
                query.startAfter(page)
            }

            val querySnapshot = currentPageQuery.get().await()

            val articleList: MutableList<Article> = mutableListOf()
            var lastVisibleProduct: DocumentSnapshot? = null

            for (document in querySnapshot.documents) {
                val name = document.getString("name").toString()
                val desc = document.getString("desc").toString()
                val benefits = document.getString("benefits").toString()
                val usage = document.getString("usage").toString()
                val keywords = document.getString("keywords").toString()
                val photoUrl = document.getString("photoUrl").toString()
                val plantCare = document.getString("plantCare").toString()


                val article = Article(name, desc, benefits, usage, keywords, plantCare ,photoUrl)

                articleList.add(article)
            }

            LoadResult.Page(
                data = articleList,
                prevKey = null,
                nextKey = lastVisibleProduct
            )
        } catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Article>): DocumentSnapshot? {
        return null
    }

}