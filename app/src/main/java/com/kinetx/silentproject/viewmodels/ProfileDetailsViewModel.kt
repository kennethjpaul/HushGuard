package com.kinetx.silentproject.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kinetx.silentproject.fragments.ProfileDetailsFragmentArgs

class ProfileDetailsViewModel(application: Application, private val argList : ProfileDetailsFragmentArgs) : AndroidViewModel(application) {

    private var _fragmentTitle = MutableLiveData<String>()
    val fragmentTitle : LiveData<String>
        get() = _fragmentTitle


    init {

        if (argList.profileId==-1L)
        {
            _fragmentTitle.value = "Create Profile"
        }
        else
        {
            _fragmentTitle.value = "Create Profile"
        }




    }
}