package com.iftah.herbflora.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.iftah.herbflora.R
import com.iftah.herbflora.databinding.ActivityLoginBinding
import com.iftah.herbflora.home.MainActivity
import com.iftah.herbflora.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        addTextChangeListener(binding.email) { binding.emailLayout.error = null }
        addTextChangeListener(binding.password) { binding.passwordLayout.error = null }

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth
        db = Firebase.firestore

        binding.signInGoogle.setOnClickListener {
            loginWithGoogleAccount()
        }

        loginViewModel.signInResult.observe(this) { resultLauncher.launch(it) }
        loginViewModel.updateUI.observe(this) { updateUI(it) }
        loginViewModel.message.observe(this) { error(it) }
        loginViewModel.isLoading.observe(this){ showLoading(it) }

        binding.login.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            loginWithEmail(email, password)
        }
        binding.signup.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginWithEmail(email: String, password: String){
        if (validateInput()) {
            loginViewModel.login(email, password)
        }
    }

    private fun loginWithGoogleAccount(){
        loginViewModel.googleSignInClient(googleSignInClient)
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                loginViewModel.firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun addTextChangeListener(editText: EditText, callback: () -> Unit) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                callback.invoke()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun validateInput(): Boolean {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        var isValid = true

        if (email.isEmpty()) {
            binding.emailLayout.error = getString(R.string.email_error)
            isValid = false
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = getString(R.string.empty_pass_error)
            isValid = false
        }

        return isValid
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun error(message: String){
        if (message.equals(getString(R.string.firebase_connection_error))){
            Toast.makeText(this, getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
        }else if(message.equals(getString(R.string.incorrect_password_firebase))){
            Toast.makeText(this, getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show()
        }
    }
}