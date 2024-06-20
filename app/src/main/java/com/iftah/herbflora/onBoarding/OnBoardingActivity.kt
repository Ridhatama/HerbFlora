package com.iftah.herbflora.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.iftah.herbflora.adapter.SectionPagerAdapter
import com.iftah.herbflora.login.LoginActivity
import com.iftah.herbflora.databinding.ActivityOnBoardingBinding
import com.iftah.herbflora.home.MainActivity

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val adapter = SectionPagerAdapter(this)

        binding.viewPager2.adapter = adapter
        binding.dotsIndicator.attachTo(binding.viewPager2)
        binding.skip.setOnClickListener {
            binding.viewPager2.setCurrentItem(2, true)
        }
        binding.next.setOnClickListener {
            binding.viewPager2.setCurrentItem(adapter.getItemId(binding.viewPager2.currentItem).toInt() + 1, true)
        }
        binding.getStarted.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 2) {
                    binding.skip.visibility = View.GONE
                    binding.next.visibility = View.GONE
                    binding.getStarted.visibility = View.VISIBLE
                } else {
                    binding.skip.visibility = View.VISIBLE
                    binding.next.visibility = View.VISIBLE
                    binding.getStarted.visibility = View.GONE
                }

            }
        })
    }



    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}