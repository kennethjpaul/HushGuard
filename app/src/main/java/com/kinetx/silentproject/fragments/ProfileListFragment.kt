package com.kinetx.silentproject.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kinetx.silentproject.R
import com.kinetx.silentproject.databinding.FragmentProfileListBinding
import com.kinetx.silentproject.recyclerview.ProfileListAdapter
import com.kinetx.silentproject.viewmodelfactories.ProfileListViewModelFactory
import com.kinetx.silentproject.viewmodels.ProfileListViewModel


class ProfileListFragment : Fragment(), ProfileListAdapter.ProfileListAdapterInterface {

    private lateinit var binding : FragmentProfileListBinding
    private lateinit var viewModel : ProfileListViewModel

    private lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false


    lateinit var notificationManager :NotificationManager
    lateinit var sharedPref : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile_list,container,false)

        val application = requireNotNull(this.activity).application

        sharedPref =  PreferenceManager.getDefaultSharedPreferences(application)

        val viewModelFactory = ProfileListViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory)[ProfileListViewModel::class.java]

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {
            isReadPermissionGranted = it[Manifest.permission.READ_CONTACTS] ?: isReadPermissionGranted
            isWritePermissionGranted = it[Manifest.permission.WRITE_CONTACTS] ?: isWritePermissionGranted
            restartApp()
        }


        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            requestNotificationPolicyAccessLauncher.launch(intent)
        }
        else
        {
            checkPermissions()
        }




        binding.profileListViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = ProfileListAdapter(this)
        binding.profileListRecycler.layoutManager = LinearLayoutManager(context)
        binding.profileListRecycler.setHasFixedSize(true)
        binding.profileListRecycler.adapter = adapter

        binding.addProfileButton.setOnClickListener()
        {
            view?.findNavController()?.navigate(
                ProfileListFragmentDirections.actionProfileListFragmentToProfileDetailsFragment(-1)
            )
        }


        viewModel.groupDatabase.observe(viewLifecycleOwner)
        {
            if(isReadPermissionGranted && isWritePermissionGranted) {
                viewModel.queryPhone(it)
            }
        }

        viewModel.profileDatabase.observe(viewLifecycleOwner)
        {
            viewModel.makeList(it)
        }

        viewModel.profileList.observe(viewLifecycleOwner)
        {
            adapter.setData(it)
        }

        viewModel.dndStatus.observe(viewLifecycleOwner)
        {
            viewModel.updateProfileListVisibility(it)
        }





        return binding.root
    }


    private fun restartApp()
    {
        if (isReadPermissionGranted && isReadPermissionGranted) {
            Toast.makeText(
                context,
                "Please restart the app for the changes to take place",
                Toast.LENGTH_LONG
            ).show()
        }
        else
        {
            Toast.makeText(context,"Please give the necessary permissions",Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeUI() {
        binding.homeDndSwitch.visibility = View.VISIBLE
        binding.addProfileButton.visibility = View.VISIBLE
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
        else
        {
            initializeUI()
        }

    }

    override fun profileListSwitchClick(position: Int) {
        Log.i("III","Switch click $position")
        viewModel.activateProfile(position)

    }

    override fun profileListLongClick(position: Int) {
        val profileId = viewModel.profileList.value?.get(position)!!.profileId
        view?.findNavController()?.navigate(ProfileListFragmentDirections.actionProfileListFragmentToProfileDetailsFragment(profileId))
    }

    override fun removeFavorites(position: Int) {
        val editor = sharedPref.edit()
        editor.putLong("last_profile",-1L)
        editor.apply()
        viewModel.removeFavorites()
    }


}