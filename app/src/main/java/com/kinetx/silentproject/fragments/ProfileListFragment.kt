package com.kinetx.silentproject.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.kinetx.silentproject.R
import com.kinetx.silentproject.databinding.FragmentProfileListBinding
import com.kinetx.silentproject.viewmodelfactories.ProfileListViewModelFactory
import com.kinetx.silentproject.viewmodels.ProfileListViewModel


class ProfileListFragment : Fragment() {

    private lateinit var binding : FragmentProfileListBinding
    private lateinit var viewModel : ProfileListViewModel

    private lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {
            isReadPermissionGranted = it[Manifest.permission.READ_CONTACTS] ?: isReadPermissionGranted
            isWritePermissionGranted = it[Manifest.permission.WRITE_CONTACTS] ?: isWritePermissionGranted
        }

        checkPermissions()




        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile_list,container,false)

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ProfileListViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory)[ProfileListViewModel::class.java]

        binding.profileListViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner



        binding.addProfileButton.setOnClickListener()
        {
            view?.findNavController()?.navigate(
                ProfileListFragmentDirections.actionProfileListFragmentToProfileDetailsFragment(-1)
            )
        }


        viewModel.groupDatabase.observe(viewLifecycleOwner)
        {
            viewModel.queryPhone(it)
        }


        return binding.root
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