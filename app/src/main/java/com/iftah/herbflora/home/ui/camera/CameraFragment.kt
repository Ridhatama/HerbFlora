package com.iftah.herbflora.home.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.iftah.herbflora.R
import com.iftah.herbflora.article.ArticleActivity
import com.iftah.herbflora.databinding.FragmentCameraBinding
import com.iftah.herbflora.home.ui.createCustomTempFile
import com.iftah.herbflora.model.Article
import java.io.File

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentPhotoPath: String
    private var getUri: Uri? = null
    private lateinit var cameraViewModel: CameraViewModel

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                requireActivity().finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            getUri = selectedImg

            binding.image.setPadding(0, 0, 0, 0)
            binding.image.setImageURI(getUri)
            clasify()
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            val fileUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.iftah.herbflora",
                myFile
            )

            getUri = fileUri

            binding.image.setPadding(0, 0, 0, 0)
            binding.image.setImageURI(getUri)
            clasify()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentCameraBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraViewModel = ViewModelProvider(this)[CameraViewModel::class.java]

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.scan.setOnClickListener {
            showOptionsDialog()
        }
    }

    private fun clasify(){
        val drawable = binding.image.drawable
        if (drawable != null){
            val bitmap = drawable.toBitmap()
            cameraViewModel.predictImage(bitmap)
            showOptionDialogResult()
        }else{
            Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showOptionsDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.options_scan_layout, null)

        val cameraButton: ImageView = dialogView.findViewById(R.id.camera)
        val galleryButton: ImageView = dialogView.findViewById(R.id.gallery)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.custom_dialog))
        dialog.show()

        cameraButton.setOnClickListener {
            dialog.dismiss()
            pickImageFromCamera()
        }

        galleryButton.setOnClickListener {
            dialog.dismiss()
            pickImageFromGallery()
        }

        dialog.setOnCancelListener {
            dialog.dismiss()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showOptionDialogResult() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.options_result_layout, null)

        val articleButton: Button = dialogView.findViewById(R.id.article)
        val predictName: TextView = dialogView.findViewById(R.id.result)
        val predictConfident: TextView = dialogView.findViewById(R.id.confident)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setLayout(20,30)
        dialog.window?.setBackgroundDrawable(requireActivity().getDrawable(R.drawable.custom_dialog))
        dialog.show()

        cameraViewModel.predictedLabel.observe(requireActivity()){ predictName.text = it }
        cameraViewModel.predictedPercentage.observe(requireActivity()){ predictConfident.text = it }

        articleButton.setOnClickListener {
            val intent = Intent(requireContext(), ArticleActivity::class.java)
            intent.putExtra("Name Article", predictName.text.toString())
            dialog.dismiss()
            startActivity(intent)
        }

        dialog.setOnCancelListener {
            dialog.dismiss()
        }
    }

    private fun Any.toBitmap(): Bitmap? {
        return when (this) {
            is Bitmap -> this
            is Drawable -> {
                val bitmap = Bitmap.createBitmap(
                    intrinsicWidth,
                    intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                setBounds(0, 0, canvas.width, canvas.height)
                draw(canvas)
                bitmap
            }
            else -> null
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun pickImageFromCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireContext().packageManager)

        createCustomTempFile(requireContext().applicationContext).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.iftah.herbflora",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getUri = null
        _binding = null

    }
}