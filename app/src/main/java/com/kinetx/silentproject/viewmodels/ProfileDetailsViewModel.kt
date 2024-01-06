package com.kinetx.silentproject.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kinetx.silentproject.database.DatabaseMain
import com.kinetx.silentproject.database.DatabaseRepository
import com.kinetx.silentproject.database.GroupDatabase
import com.kinetx.silentproject.database.ProfileDatabase
import com.kinetx.silentproject.dataclass.GroupsRecyclerDataClass
import com.kinetx.silentproject.fragments.ProfileDetailsFragmentArgs
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@SuppressLint("Range", "Recycle")
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

    private var _profileQuery = MutableLiveData<ProfileDatabase>()
    val profileQuery : LiveData<ProfileDatabase>
        get() = _profileQuery

    var profile : ProfileDatabase = ProfileDatabase()
    var profileName = MutableLiveData<String>()

    val groupDatabase : LiveData<List<GroupDatabase>>
    private var groupData : List<GroupDatabase> = emptyList()

    private var _adapterQuery = MutableLiveData<List<GroupsRecyclerDataClass>>()
    val adapterQuery : LiveData<List<GroupsRecyclerDataClass>>
        get() = _adapterQuery


    private val repository : DatabaseRepository

    init {

        val userDao = DatabaseMain.getInstance(application).databaseDao
        repository = DatabaseRepository(userDao)
        groupDatabase = repository.getAllGroups

        if (argList.profileId==-1L)
        {
            _fragmentTitle.value = "Create Profile"
            _isCreateVisible.value = View.VISIBLE
            _isUpdateVisible.value = View.GONE
        }
        else
        {
            _fragmentTitle.value = "Edit Profile"
            _isCreateVisible.value = View.GONE
            _isUpdateVisible.value = View.VISIBLE

            viewModelScope.launch(Dispatchers.IO)
            {
                _profileQuery.postValue(repository.getProfileWithId(argList.profileId))
            }

        }


    }

    fun updateGroupData(it: List<GroupDatabase>) {
        groupData = it
        updateList()
    }

    fun updateProfileData(it: ProfileDatabase) {
        profile = it
        profileName.value = it.profileName
        updateList()
    }


    private fun updateList() {
        var groupListProfile = profile.groupIds
        val _list : ArrayList<GroupsRecyclerDataClass> = ArrayList()


        groupData.filter {
            groupListProfile.contains(it.groupId)
        }.groupBy {
            it.groupName
        }.forEach() {
            Log.i("TTT ","Checked")
            _list.add(GroupsRecyclerDataClass(true,it.key,it.value.map { it.groupId }))
        }

        groupData.filterNot {
            groupListProfile.contains(it.groupId)
        }.groupBy {
            it.groupName
        }.forEach() {
            Log.i("TTT ","un Checked")
            _list.add(GroupsRecyclerDataClass(false,it.key,it.value.map { it.groupId }))
        }
        _adapterQuery.value = _list
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun createProfile(): Boolean {

        profile.profileId = 0L
        profile.profileIcon = "profile"
        profile.profileColor = java.lang.Long.decode("0xFF005252").toInt()

        profile.groupIds = _adapterQuery.value?.filter { it.isChecked }?.map { it.groupId }!!.flatten().distinctBy {
            it
        }

        if(checkProfile(profile))
        {
            //TODO relational database
            GlobalScope.launch(Dispatchers.IO)
            {
                repository.insertProfile(profile)
            }
            return true
        }

        return false
    }



    @OptIn(DelicateCoroutinesApi::class)
    fun updateProfile(): Boolean {

        profile.profileIcon = "profile"
        profile.profileColor = java.lang.Long.decode("0xFF005252").toInt()

        profile.groupIds = _adapterQuery.value?.filter { it.isChecked }?.map { it.groupId }!!.flatten().distinctBy {
            it
        }

        if(checkProfile(profile))
        {
            //TODO relational database
            GlobalScope.launch(Dispatchers.IO)
            {
                repository.updateProfile(profile)
            }
            return true
        }

        return false
    }



    private fun checkProfile(profile: ProfileDatabase): Boolean {

        val context = getApplication<Application>().applicationContext

        if (profile.profileName=="")
        {
            Toast.makeText(context,"Profile name cannot be blank",Toast.LENGTH_SHORT).show()
            return false
        }
        if (profile.groupIds.isEmpty())
        {
            Toast.makeText(context,"Select atleast one group",Toast.LENGTH_SHORT).show()
            return false
        }


        return true
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun deleteProfile() {
        GlobalScope.launch(Dispatchers.IO)
        {
            repository.deleteProfile(profile)
        }
    }

}