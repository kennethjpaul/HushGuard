package com.kinetx.silentproject.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.kinetx.silentproject.R
import com.kinetx.silentproject.databinding.FragmentProfileListBinding
import com.kinetx.silentproject.viewmodelfactories.ProfileListViewModelFactory
import com.kinetx.silentproject.viewmodels.ProfileListViewModel


class ProfileListFragment : Fragment() {

    private lateinit var binding : FragmentProfileListBinding
    private lateinit var viewModel : ProfileListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile_list,container,false)

        val application = requireNotNull(this.activity).application
        val viewModelFactory = ProfileListViewModelFactory(application)
        viewModel = ViewModelProvider(this,viewModelFactory)[ProfileListViewModel::class.java]

        binding.profileListViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner



        return binding.root
    }


}