package com.iftah.herbflora.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.iftah.herbflora.onBoarding.OnBoardingFragment1
import com.iftah.herbflora.onBoarding.OnBoardingFragment2
import com.iftah.herbflora.onBoarding.OnBoardingFragment3

class SectionPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> OnBoardingFragment1()
            1 -> OnBoardingFragment2()
            else -> OnBoardingFragment3()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}