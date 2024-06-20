package com.iftah.herbflora.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.iftah.herbflora.model.Article

class ArticleViewModel: ViewModel() {

    private val _article = MutableLiveData<Article>()
    val article: LiveData<Article> get() = _article

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message


    fun getDataArticle(nameArticle: String){
        _isLoading.value = true

        val firestore = FirebaseFirestore.getInstance()
        val collectionRef = firestore.collection("article").document(nameArticle)

        collectionRef.get().addOnSuccessListener {
            if (it != null){
                val name = it.getString("name").toString()
                val desc = it.getString("desc").toString()
                val benefits = it.getString("benefits").toString().replace("\\n", "\n \n")
                val usage = it.getString("usage").toString().replace("\\n", "\n \n")
                val keywords = it.getString("keywords").toString()
                val plantCare = it.getString("plantCare").toString().replace("\\n", "\n \n")
                val photoUrl = it.getString("photoUrl").toString()

                val data = Article(name, desc, benefits, usage, keywords, plantCare ,photoUrl)

                _article.value = data
                _isLoading.value = false
            }
        }.addOnFailureListener {
            _message.value = it.message
            _isLoading.value = false
        }

    }
}