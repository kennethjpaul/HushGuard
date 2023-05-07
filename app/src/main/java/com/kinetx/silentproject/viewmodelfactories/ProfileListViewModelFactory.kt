package com.kinetx.silentproject.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kinetx.silentproject.viewmodels.ProfileListViewModel

class ProfileListViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ProfileListViewModel::class.java))
        {
            return ProfileListViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}