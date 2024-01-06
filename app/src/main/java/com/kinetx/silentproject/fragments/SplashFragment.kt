package com.kinetx.silentproject.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.kinetx.silentproject.R
import kotlinx.coroutines.*

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment



        val sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val onBoardingFinished = sharedPref.getBoolean("onBoardingFinished",false)
//        val coroutineScope = CoroutineScope(Dispatchers.Main)
//        coroutineScope.launch(Dispatchers.IO)
//        {
//            delay(1000)
//            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToProfileListFragment())
//        }

        Handler().postDelayed(
            {
                if (onBoardingFinished)
                {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToProfileListFragment())
                }
                else
                {
                    findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToViewPagerFragment())
                }
            },1000
        )

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


}