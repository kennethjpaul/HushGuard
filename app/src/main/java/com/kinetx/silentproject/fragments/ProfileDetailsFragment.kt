package com.kinetx.silentproject.fragments

import android.app.AlertDialog
import android.app.AlertDialog.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kinetx.silentproject.R
import com.kinetx.silentproject.databinding.FragmentProfileDetailsBinding
import com.kinetx.silentproject.helpers.Converters
import com.kinetx.silentproject.recyclerview.GroupListAdapter
import com.kinetx.silentproject.viewmodelfactories.ProfileDetailsViewModelFactory
import com.kinetx.silentproject.viewmodels.ProfileDetailsViewModel


class ProfileDetailsFragment : Fragment() {

    private lateinit var binding : FragmentProfileDetailsBinding
    private lateinit var viewModel : ProfileDetailsViewModel
    private lateinit var argList : ProfileDetailsFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        argList = ProfileDetailsFragmentArgs.fromBundle(requireArguments())
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile_details,container,false)

        val application = requireNotNull(this.activity).application

        val viewModelFactory = ProfileDetailsViewModelFactory(application,argList)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileDetailsViewModel::class.java]


        binding.profileDetailsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        val adapter = GroupListAdapter()
        binding.profileDetailsSelectedProfiles.layoutManager = LinearLayoutManager(context)
        binding.profileDetailsSelectedProfiles.setHasFixedSize(true)
        binding.profileDetailsSelectedProfiles.adapter = adapter


        binding.profileDetailsCreateButton.setOnClickListener()
        {
            if(viewModel.createProfile())
            {
                view?.findNavController()?.navigateUp()
            }
        }

        binding.profileDetailsUpdateButton.setOnClickListener()
        {
            if (viewModel.updateProfile())
            {
                view?.findNavController()?.navigateUp()
            }
        }

        binding.profileDetailsDeleteButton.setOnClickListener()
        {
            val builder = Builder(requireContext())
            builder.setPositiveButton("Yes")
            {
                    _,_ ->
                viewModel.deleteProfile()
                view?.findNavController()?.navigateUp()
            }
            builder.setNegativeButton("No")
            {
                    _,_ ->
            }
            builder.setTitle("Do you want to delete this profile")
            builder.setMessage("This action is permanent")
            builder.create().show()
        }



        viewModel.fragmentTitle.observe(viewLifecycleOwner)
        {
            (activity as AppCompatActivity).supportActionBar?.title = it
        }

        viewModel.profileName.observe(viewLifecycleOwner)
        {
            viewModel.profile.profileName = it
        }

        viewModel.groupDatabase.observe(viewLifecycleOwner)
        {
           viewModel.updateGroupData(it)
        }

        viewModel.profileQuery.observe(viewLifecycleOwner)
        {
            binding.profileDetailsIcon.setImageResource(Converters.getResourceInt(application,it.profileIcon))
            binding.profileColorButton.setCardBackgroundColor(it.profileColor)
            viewModel.updateProfileData(it)

        }

        viewModel.adapterQuery.observe(viewLifecycleOwner)
        {
            adapter.setData(it)
        }


        return binding.root
    }

}