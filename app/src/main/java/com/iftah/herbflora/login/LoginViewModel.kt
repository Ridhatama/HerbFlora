package com.iftah.herbflora.login

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel() : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _signInResult = MutableLiveData<Intent>()
    val signInResult: LiveData<Intent> = _signInResult

    private val _updateUI = MutableLiveData<FirebaseUser>()
    val updateUI: LiveData<FirebaseUser> get() = _updateUI

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun googleSignInClient(googleSignInClient: GoogleSignInClient) {
        val signInIntent = googleSignInClient.signInIntent
        _signInResult.value = signInIntent
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val auth = FirebaseAuth.getInstance()
                    val data = hashMapOf(
                        "uid" to auth.currentUser?.uid,
                        "access role" to "user",
                        "name" to auth.currentUser?.displayName
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

                    _updateUI.value = auth.currentUser
                }
            }.addOnFailureListener {
                _message.value = it.message
            }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _updateUI.value = auth.currentUser
                    _isLoading.value = false
                } else {
                    _isLoading.value = false
                }
            }
            .addOnFailureListener {
                _message.value = it.message.toString()
                _isLoading.value = false
            }
    }
}