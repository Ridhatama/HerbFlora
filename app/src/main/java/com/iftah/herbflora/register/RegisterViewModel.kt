package com.iftah.herbflora.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel() : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _updateUI = MutableLiveData<FirebaseUser?>()
    val updateUI: LiveData<FirebaseUser?> get() = _updateUI

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun registerUser(email: String, password: String, name: String){
        _isLoading.value = true
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    val user = auth.currentUser

                    val data = hashMapOf(
                        "uid" to user?.uid,
                        "access role" to "user",
                        "name" to name
                    )

                    val docId = auth.currentUser?.uid.toString()

                    db.collection("users").document(docId)
                        .get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                Log.d("Firestore", "Already exists")
                            } else {
                                db.collection("users").document(docId).set(data)
                            }
                        }
                    _isLoading.value = false
                    _updateUI.value = user
                }
            }
            .addOnFailureListener {
                _message.value = it.message
                _isLoading.value = false
            }
    }
}