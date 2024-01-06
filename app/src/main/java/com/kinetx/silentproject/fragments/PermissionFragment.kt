package com.kinetx.silentproject.fragments

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.kinetx.silentproject.R
import com.kinetx.silentproject.onboarding.ViewPagerFragmentDirections

class PermissionFragment : Fragment() {

    private lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false


    lateinit var notificationManager : NotificationManager
    lateinit var sharedPref : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_permission, container, false)

        val application = requireNotNull(this.activity).application
        sharedPref =  PreferenceManager.getDefaultSharedPreferences(application)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {
            isReadPermissionGranted = it[Manifest.permission.READ_CONTACTS] ?: isReadPermissionGranted
            isWritePermissionGranted = it[Manifest.permission.WRITE_CONTACTS] ?: isWritePermissionGranted
        }

        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        launchNotificationPermissionCheck()

        val permissionButton : Button = view.findViewById(R.id.permission_permissions)

        permissionButton.setOnClickListener()
        {
            launchNotificationPermissionCheck()
        }

        val finishButton : Button = view.findViewById(R.id.permission_finish)

        finishButton.setOnClickListener()
        {
            if (isReadPermissionGranted && isWritePermissionGranted && notificationManager.isNotificationPolicyAccessGranted )
            {
                view?.findNavController()?.navigate(PermissionFragmentDirections.actionPermissionFragmentToProfileListFragment())
            }
            else
            {
                Toast.makeText(context,"Please ask for permissions and allow the required permissions",
                    Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }


    private fun launchNotificationPermissionCheck() {
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            requestNotificationPolicyAccessLauncher.launch(intent)
        }
        else
        {
            checkPermissions()
        }
    }

    private val requestNotificationPolicyAccessLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (notificationManager?.isNotificationPolicyAccessGranted == true) {
                checkPermissions()
            } else {
                Log.i("III","Here 2")
            }
        }

    private fun checkPermissions() {
        isReadPermissionGranted = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.READ_CONTACTS
        )== PackageManager.PERMISSION_GRANTED
        isWritePermissionGranted = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.WRITE_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED


        val permissionRequest : MutableList<String> = ArrayList()

        if (!isReadPermissionGranted)
        {
            permissionRequest.add(Manifest.permission.READ_CONTACTS)
        }

        if (!isWritePermissionGranted)
        {
            permissionRequest.add(Manifest.permission.WRITE_CONTACTS)
        }

        if(permissionRequest.isNotEmpty())
        {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }

    }

}