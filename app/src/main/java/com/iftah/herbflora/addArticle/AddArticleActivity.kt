package com.iftah.herbflora.addArticle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.iftah.herbflora.R
import com.iftah.herbflora.databinding.ActivityAddArticleBinding
import com.iftah.herbflora.home.MainActivity

class AddArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddArticleBinding
    private var getUri: Uri? = null
    private lateinit var addArticleViewModel: AddArticleViewModel

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            getUri = selectedImg

            binding.photo.setPadding(0, 0, 0, 0)
            binding.photo.setImageURI(getUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.add_post)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addArticleViewModel = ViewModelProvider(this)[AddArticleViewModel::class.java]
        addArticleViewModel.message.observe(this){ Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show() }
        addArticleViewModel.isLoading.observe(this){ showLoading(it) }
        addArticleViewModel.updateUI.observe(this){ updateUI(it) }

        addTextChangeListener(binding.name){ binding.nameLayout.error = null }
        addTextChangeListener(binding.desc){ binding.descLayout.error = null }
        addTextChangeListener(binding.benefits){ binding.benefitsLayout.error = null }
        addTextChangeListener(binding.usage){ binding.usageLayout.error = null }
        addTextChangeListener(binding.keywords){ binding.keywordsLayout.error = null }
        addTextChangeListener(binding.keywords){ binding.plantCareLayout.error = null }

        binding.photo.setOnClickListener {
            pickImageFromGallery()
        }

        binding.post.setOnClickListener {
            val desc = binding.desc.text.toString().trim()
            val usage = binding.usage.text.toString().trim()
            val name = binding.name.text.toString().trim()
            val benefits = binding.benefits.text.toString().trim()
            val keywords = binding.keywords.text.toString().trim()
            val plantCare = binding.plantCare.text.toString().trim()
            if (validateInput(desc, usage, name, benefits, keywords, plantCare)){
                addArticleViewModel.addArticle(name, desc, benefits,
                    usage, getUri, keywords, plantCare)
            }
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

    private fun validateInput(desc : String, usage : String, name : String, benefits : String, keywords : String, plantCare : String): Boolean {
        var isValid = true

        if (desc.isEmpty()) {
            binding.descLayout.error = getString(R.string.description_is_not_allowed_to_be_empty)
            isValid = false
        }

        if (name.isEmpty()){
            binding.nameLayout.error = getString(R.string.name_error)
        }

        if (usage.isEmpty()) {
            binding.usageLayout.error = getString(R.string.usage_field_cannot_be_left_empty)
            isValid = false
        }

        if (benefits.isEmpty()) {
            binding.benefitsLayout.error = getString(R.string.benefits_field_cannot_be_left_empty)
            isValid = false
        }

        if (keywords.isEmpty()) {
            binding.keywordsLayout.error = getString(R.string.keywords_field_cannot_be_left_empty)
            isValid = false
        }

        if (plantCare.isEmpty()){
            binding.plantCareLayout.error =
                getString(R.string.plant_care_field_cannot_be_left_empty)
            isValid = false
        }

        if (getUri == null){
            Toast.makeText(this, getString(R.string.please_insert_the_image), Toast.LENGTH_SHORT).show()
        }

        return isValid
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun updateUI(state: Boolean) {
        if (state) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}