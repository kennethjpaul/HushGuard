package com.kinetx.silentproject.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kinetx.silentproject.fragments.ProfileDetailsFragmentArgs

class ProfileDetailsViewModel(application: Application, private val argList : ProfileDetailsFragmentArgs) : AndroidViewModel(application) {

    private var _fragmentTitle = MutableLiveData<String>()
    val fragmentTitle : LiveData<String>
        get() = _fragmentTitle

    private var _isCreateVisible = MutableLiveData<Int>()
    val isCreateVisible : LiveData<Int>
        get() = _isCreateVisible


    private var _isUpdateVisible = MutableLiveData<Int>()
    val isUpdateVisible : LiveData<Int>
        get() = _isUpdateVisible


    init {

        if (argList.profileId==-1L)
        {
            _fragmentTitle.value = "Create Profile"
            _isCreateVisible.value = View.VISIBLE
            _isUpdateVisible.value = View.GONE
        }
        else
        {
            _fragmentTitle.value = "Create Profile"
            _isCreateVisible.value = View.GONE
            _isUpdateVisible.value = View.VISIBLE
        }




    }
}