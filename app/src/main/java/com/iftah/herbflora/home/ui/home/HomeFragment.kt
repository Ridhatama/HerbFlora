package com.iftah.herbflora.home.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.iftah.herbflora.adapter.HomePagingAdapter
import com.iftah.herbflora.R
import com.iftah.herbflora.databinding.FragmentHomeBinding
import com.iftah.herbflora.onBoarding.OnBoardingActivity
import com.iftah.herbflora.repository.Repository

class HomeFragment : Fragment(), MenuProvider {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbar
        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)?.supportActionBar?.title = "Home"

        val repository = Repository()
        val viewModelFactory = HomeViewModelFactory(repository)
        val homeViewModel =
            ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        binding.recycleView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        getData(homeViewModel)

        binding.cardArticle.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_list_article)
        }

        binding.cardCamera.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_camera)
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.logout){
            logout()
            return true
        }
        return false
    }

    private fun logout(){
        try {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireActivity(), OnBoardingActivity::class.java))
            requireActivity().finish()
        } catch (e: Exception){
            Toast.makeText(requireContext(), getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }


    private fun getData(viewModel: HomeViewModel) {
        val adapter = HomePagingAdapter()
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