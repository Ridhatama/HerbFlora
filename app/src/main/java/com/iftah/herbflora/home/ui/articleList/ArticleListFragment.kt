package com.iftah.herbflora.home.ui.articleList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.iftah.herbflora.adapter.ArticlePagingAdapter
import com.iftah.herbflora.addArticle.AddArticleActivity
import com.iftah.herbflora.databinding.FragmentArticleBinding
import com.iftah.herbflora.repository.Repository

class ArticleListFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbar
        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Article"

        val repository = Repository()
        val viewModelFactory = ArticleViewModelFactory(repository)
        val notificationsViewModel =
            ViewModelProvider(this, viewModelFactory)[ArticleViewModel::class.java]

        binding.recycleView.layoutManager = LinearLayoutManager(context)

        val auth = Firebase.auth
        val firestore = Firebase.firestore

        firestore.collection("users").document(auth.currentUser?.uid.toString())
            .get().addOnSuccessListener { documentSnapshot ->
                val role = documentSnapshot.getString("access role").toString()

                if (role == "admin") {
                    binding.fab.visibility = View.VISIBLE
                } else {
                    binding.fab.visibility = View.GONE
                }
                binding.fab.setOnClickListener {
                    startActivity(Intent(requireContext(), AddArticleActivity::class.java))
                }
            }
        


        getData(notificationsViewModel)

        return binding.root
    }

    private fun getData(viewModel: ArticleViewModel) {
        val adapter = ArticlePagingAdapter()
        binding.recycleView.adapter = adapter

        viewModel.article.observe(requireActivity()) {
            adapter.submitData(lifecycle, it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}