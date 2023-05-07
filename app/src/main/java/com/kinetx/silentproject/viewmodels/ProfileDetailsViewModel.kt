package com.kinetx.silentproject.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
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
                //Log.i("TTT","$contactName : $contactId ")

                tmp.add(GroupList(contactName,contactId))

            }

            _allGroupList.value = tmp

        }


    }


    fun databaseQuery() {
        val id : Long = argList.profileId
        _databaseList.value = emptyList()

    }

    fun adapterList() {
        val m = _allGroupList.value?.distinctBy {
            it.groupName
        }

        if (m != null) {
            val tmp = ArrayList<GroupsRecyclerDataClass>()
            for (i in m)
            {
                tmp.add(GroupsRecyclerDataClass(false,i.groupName,i.groupId))
            }
            _adapterList.value = tmp
        }



    }

}