package com.iftah.herbflora.addArticle

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddArticleViewModel : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _updateUI = MutableLiveData<Boolean>()
    val updateUI: LiveData<Boolean> get() = _updateUI


    fun addArticle(name: String, desc: String, benefits: String, usage: String, uri: Uri?, keywords: String, plantCare: String) {
        _isLoading.value = true
        if (uri != null) {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imagesRef: StorageReference = storageRef.child(name).child("article")
            val fileName = "${System.currentTimeMillis()}.jpg"
            val imageRef = imagesRef.child(fileName)
            val uploadTask = imageRef.putFile(uri)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnCompleteListener {
                    val downloadUri = it.result.toString()
                    val db = Firebase.firestore
                    val article = hashMapOf(
                        "name" to name,
                        "desc" to desc,
                        "photoUrl" to downloadUri,
                        "benefits" to benefits,
                        "usage" to usage,
                        "keywords" to keywords,
                        "plantCare" to plantCare
                    )

                    db.collection("article").document(name)
                        .set(article)

                    _isLoading.value = false
                    _updateUI.value = true
                }.addOnFailureListener {
                    _message.value = it.message
                    _isLoading.value = false
                    _updateUI.value = false
                }
            }
        }
    }


}