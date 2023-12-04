package com.kinetx.silentproject.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kinetx.silentproject.dataclass.GroupList
import com.kinetx.silentproject.dataclass.GroupsRecyclerDataClass
import com.kinetx.silentproject.fragments.ProfileDetailsFragmentArgs

@SuppressLint("Range")
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


    private var _allGroupList = MutableLiveData<List<GroupList>>()
    val allGroupList : LiveData<List<GroupList>>
        get() = _allGroupList


    private var _databaseList = MutableLiveData<List<GroupList>>()
    val databaseList : LiveData<List<GroupList>>
        get() = _databaseList


    private var _adapterList = MutableLiveData<List<GroupsRecyclerDataClass>>()
    val adapterList : LiveData<List<GroupsRecyclerDataClass>>
        get() = _adapterList


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



        val groupCursor = application.contentResolver.query(
            ContactsContract.Groups.CONTENT_URI, arrayOf(
                ContactsContract.Groups._ID,
                ContactsContract.Groups.TITLE
            ), "${ContactsContract.Groups.GROUP_VISIBLE} LIKE '1' AND ${ContactsContract.Groups.GROUP_IS_READ_ONLY} LIKE '0'", null, null
        )

        if (groupCursor != null) {
            val tmp = ArrayList<GroupList>()

            while (groupCursor.moveToNext())
            {
                val contactName : String = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.Groups.TITLE))
                val contactId : Long = groupCursor.getLong(groupCursor.getColumnIndex(ContactsContract.Groups._ID))
                Log.i("TTT","$contactName : $contactId ")

                tmp.add(GroupList(contactName,contactId))

            }

            _allGroupList.value = tmp

        }


    }

    //So what is happening here is this. Initially when the fragment is launched, it retrieves the list of all the groups. That triggers the databaseQuery() which returns the list of groups that were selected. Once this list is updated, it triggers the adapterList() function which updates the adapter list.

    fun databaseQuery() {
        val id : Long = argList.profileId

        var tmp = ArrayList<GroupList>()

        tmp.add(GroupList("Family",17))

        _databaseList.value = tmp

    }

    fun adapterList() {

        // This is a list containing the name and id of all groups on the phone
        val phoneGroupList = _allGroupList.value?.distinctBy {
            it.groupId
        }

        // This is a list containing the name and id of all groups currently associated with the profile
        val profileGroupList = _databaseList.value?.distinctBy {
            it.groupId
        }

        // This is a list containing the name and id of all groups on the phone but that is not currently associated with the profile
        var filteredGroupList = listOf<GroupList>()
        if (profileGroupList != null) {
             filteredGroupList = phoneGroupList?.filter {
                 !profileGroupList.contains(it)
             }!!
        }

        // Initializing a list that can be assigned to the adapter later
        val adapterListItemList = ArrayList<GroupsRecyclerDataClass>()

        // Adding to the list design above the groups associated with the profile (checked = true)
        if (profileGroupList != null) {
            adapterListItemList.addAll(profileGroupList.map {
                GroupsRecyclerDataClass(true, it.groupName, it.groupId)
            })
        }

        // Adding to the list groups that are not associated with the profile (checked = false)
        adapterListItemList.addAll(filteredGroupList.map {
            GroupsRecyclerDataClass(false, it.groupName, it.groupId)
        })

        // Assigning the list to the adapter, if it is not empty
        if (adapterListItemList.isNotEmpty())
        {
            _adapterList.value = adapterListItemList
        }


    }

}