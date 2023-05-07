package com.kinetx.silentproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.kinetx.silentproject.R
import com.kinetx.silentproject.databinding.FragmentProfileDetailsBinding
import com.kinetx.silentproject.viewmodelfactories.ProfileDetailsViewModelFactory
import com.kinetx.silentproject.viewmodels.ProfileDetailsViewModel


class ProfileDetailsFragment : Fragment() {

    private lateinit var binding : FragmentProfileDetailsBinding
    private lateinit var viewModel : ProfileDetailsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile_details,container,false)

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ProfileDetailsViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileDetailsViewModel::class.java]


        binding.profileDetailsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

}