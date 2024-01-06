package com.kinetx.silentproject.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.kinetx.silentproject.R


class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList  = arrayListOf<Fragment>(
            OnboardFirstFragment(),
            OnboardThirdFragment(),
            OnBoardingFourthFragment(),
            OnboardSecondFragment(),
            OnBoardingFifthFragment()
        )

        val adapter= ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )


        val viewPager : ViewPager2 = view.findViewById(R.id.viewPager)

        viewPager.adapter = adapter

        return view
    }


}