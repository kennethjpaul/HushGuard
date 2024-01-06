package com.kinetx.silentproject.fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.kinetx.silentproject.R
import kotlinx.coroutines.*

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val coroutineScope = CoroutineScope(Dispatchers.Main)


//        coroutineScope.launch(Dispatchers.IO)
//        {
//            delay(1000)
//            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToProfileListFragment())
//        }

        Handler().postDelayed(
            {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToProfileListFragment())
            },1000
        )

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


}