package com.kinetx.silentproject.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kinetx.silentproject.database.DatabaseMain
import com.kinetx.silentproject.database.DatabaseRepository
import com.kinetx.silentproject.database.GroupDatabase
import com.kinetx.silentproject.database.ProfileDatabase
import com.kinetx.silentproject.dataclass.ProfileItemData
import com.kinetx.silentproject.helpers.Converters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("Range")
class ProfileListViewModel(application: Application): AndroidViewModel(application) {


    val uri2 : Uri = ContactsContract.Data.CONTENT_URI
    val contentResolver : ContentResolver = application.contentResolver

    val groupDatabase : LiveData<List<GroupDatabase>>
    val profileDatabase : LiveData<List<ProfileDatabase>>

    var profileSorted : List<ProfileDatabase> = emptyList()

    private val _groupPhone = MutableLiveData<List<GroupDatabase>>()
    val groupPhone : LiveData<List<GroupDatabase>>
        get() = _groupPhone

    private val _profileList = MutableLiveData<List<ProfileItemData>>()
    val profileList : LiveData<List<ProfileItemData>>
        get() = _profileList

    val a = application



    private val valueUnStar = ContentValues().apply {
        put(ContactsContract.Contacts.STARRED, 0);
    }

    private val valueStar = ContentValues().apply {
        put(ContactsContract.Contacts.STARRED, 1);
    }







    private val repository : DatabaseRepository

    init {

        val userDao = DatabaseMain.getInstance(application).databaseDao
        repository = DatabaseRepository(userDao)
        groupDatabase = repository.getAllGroups
        profileDatabase = repository.getAllProfiles
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
                Log.i("III", "Inserting group with id ${it.groupId} and name ${it.groupName}")
                repository.insertGroup(it)
            }


        }

    }

    fun makeList(it: List<ProfileDatabase>) {

        profileSorted = it.sortedBy { it.profileName }

        _profileList.value  = profileSorted.map {
            ProfileItemData(it.profileId,it.profileName,Converters.getResourceInt(a,it.profileIcon),it.profileColor,false)
        }
    }

    fun activateProfile(position: Int) {
        val groupList = profileSorted[position].groupIds
        val contactList : ArrayList<String> = ArrayList()
        groupList.forEach {
            val cursor2 : Cursor? = contentResolver.query(uri2,null,"${ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID} LIKE '$it'",null,null)
            if (cursor2 != null) {
                if (cursor2.count>0)
                {
                    while (cursor2.moveToNext())
                    {
                        val contactName : String = cursor2.getString(cursor2.getColumnIndex( ContactsContract.Data.DISPLAY_NAME_PRIMARY))
                        val contactId : String = cursor2.getString(cursor2.getColumnIndex( ContactsContract.Data.CONTACT_ID))
                        contactList.add(contactId)
                    }
                }

            }
        }

        removeFavorites()

        val contactIdsDistinct = contactList.distinct()

        val contactIdsString = contactIdsDistinct.joinToString(",") { it }
        val selectionStarred = "${ContactsContract.Contacts._ID} IN ($contactIdsString)"

        contentResolver.update(
            ContactsContract.Contacts.CONTENT_URI,
            valueStar,
            selectionStarred,
            null
        )


    }

    fun removeFavorites() {

        val selection = "${ContactsContract.Contacts.STARRED} = ?"
        val selectionArgs = arrayOf("1") // 1 for starred contacts

        contentResolver.update(
            ContactsContract.Contacts.CONTENT_URI,
            valueUnStar,
            selection,
            selectionArgs
        )
    }

}