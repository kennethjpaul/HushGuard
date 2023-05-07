package com.kinetx.silentproject.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kinetx.silentproject.fragments.ProfileDetailsFragmentArgs
import com.kinetx.silentproject.viewmodels.ProfileDetailsViewModel

class ProfileDetailsViewModelFactory(private val application: Application, private val argList : ProfileDetailsFragmentArgs): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileDetailsViewModel::class.java))
        {
            return ProfileDetailsViewModel(application,argList) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}