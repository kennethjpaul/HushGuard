package com.kinetx.silentproject.fragments

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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

        val isReadPermissionGranted = ContextCompat.checkSelfPermission(requireContext(),
        Manifest.permission.READ_CONTACTS
        )== PackageManager.PERMISSION_GRANTED

        val isWritePermissionGranted = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Handler().postDelayed(
            {
                if (onBoardingFinished)
                {
                    if (isReadPermissionGranted && isWritePermissionGranted && notificationManager.isNotificationPolicyAccessGranted)
                    {
                        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToProfileListFragment())
                    }
                    else
                    {
                        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToPermissionFragment())
                    }
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