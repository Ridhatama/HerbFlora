package com.iftah.herbflora.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import com.iftah.herbflora.R
import com.iftah.herbflora.databinding.ActivityRegisterBinding
import com.iftah.herbflora.home.MainActivity
import com.iftah.herbflora.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

        private lateinit var binding: ActivityRegisterBinding
        private lateinit var registerViewModel: RegisterViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityRegisterBinding.inflate(layoutInflater)
            setContentView(binding.root)

            addTextChangeListener(binding.name){
                binding.nameLayout.error = null
            }

            addTextChangeListener(binding.password){
                binding.passwordLayout.error = null
            }

            addTextChangeListener(binding.email){
                binding.emailLayout.error = null
            }

            registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

            binding.signIn.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            binding.register.setOnClickListener {
                val name = binding.name.text.toString().trim()
                val email = binding.email.text.toString().trim()
                val password = binding.password.text.toString().trim()
                register(name, email, password)
            }

            registerViewModel.message.observe(this) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }

            registerViewModel.updateUI.observe(this){ updateUI(it) }
            registerViewModel.isLoading.observe(this){ showLoading(it) }

        }

        private fun updateUI(currentUser: FirebaseUser?) {
            if (currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        private fun register(name: String, email: String, password: String){
            if (validateInput()) {
                registerViewModel.registerUser(email, password, name)
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
            val name = binding.name.text.toString().trim()
            var isValid = true

            if (email.isEmpty()) {
                binding.emailLayout.error = getString(R.string.email_error)
                isValid = false
            }

            if (name.isEmpty()){
                binding.nameLayout.error = getString(R.string.name_error)
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
}