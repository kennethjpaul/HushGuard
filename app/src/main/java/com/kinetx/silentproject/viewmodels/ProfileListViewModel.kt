package com.kinetx.silentproject.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kinetx.silentproject.database.DatabaseMain
import com.kinetx.silentproject.database.DatabaseRepository
import com.kinetx.silentproject.database.GroupDatabase
import com.kinetx.silentproject.dataclass.GroupData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("Range")
class ProfileListViewModel(application: Application): AndroidViewModel(application) {

    val groupDatabase : LiveData<List<GroupDatabase>>

    private val _groupPhone = MutableLiveData<List<GroupDatabase>>()
    val groupPhone : LiveData<List<GroupDatabase>>
        get() = _groupPhone

    val a = application

    private val repository : DatabaseRepository

    init {

        val userDao = DatabaseMain.getInstance(application).databaseDao
        repository = DatabaseRepository(userDao)
        groupDatabase = repository.getAllGroups

    }

    fun queryPhone(it: List<GroupDatabase>) {

        val groupCursor = a.contentResolver.query(
            ContactsContract.Groups.CONTENT_URI, arrayOf(
                ContactsContract.Groups._ID,
                ContactsContract.Groups.TITLE
            ), "${ContactsContract.Groups.GROUP_VISIBLE} LIKE '1' AND ${ContactsContract.Groups.GROUP_IS_READ_ONLY} LIKE '0'", null, null
        )

        val groupPhone = ArrayList<GroupDatabase>()

        if (groupCursor != null) {

            while (groupCursor.moveToNext())
            {
                val contactName : String = groupCursor.getString(groupCursor.getColumnIndex(
                    ContactsContract.Groups.TITLE))
                val contactId : Long = groupCursor.getLong(groupCursor.getColumnIndex(
                    ContactsContract.Groups._ID))
                groupPhone.add(GroupDatabase(contactId,contactName))

            }

        }

        val deletedGroups : List<GroupDatabase> = it.filterNot {
            groupPhone.map {
                it.groupId
            }.contains(it.groupId)
        }

        val newGroups : List<GroupDatabase> = groupPhone.filterNot { grp->
            it.map {
                it.groupId
            }.contains(grp.groupId)
        }


        viewModelScope.launch(Dispatchers.IO)
        {
            deletedGroups.forEach {
                Log.i("III", "Deleting group with id ${it.groupId} and name ${it.groupName}")
                repository.deleteGroup(it)
            }

            newGroups.forEach {
                repository.insertGroup(it)
            }


        }

    }

}