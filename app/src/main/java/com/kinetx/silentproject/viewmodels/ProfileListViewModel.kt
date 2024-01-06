package com.kinetx.silentproject.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationManager
import android.app.NotificationManager.*
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
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


    private val _dndStatus = MutableLiveData<Boolean>()
    val dndStatus : LiveData<Boolean>
        get() = _dndStatus

    private var _isProfileListVisible = MutableLiveData<Int>()
    val isProfileListVisible : LiveData<Int>
        get() = _isProfileListVisible

    val a = application

    var sharedPref : SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    var lastProfileId : Long = -1L

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

        lastProfileId = sharedPref.getLong("last_profile",-1L)

        val dndStatus = Settings.Global.getInt(contentResolver, "zen_mode")

        if(dndStatus==0)
        {
            lastProfileId=-1L
        }
        _dndStatus.value = when(dndStatus)
        {
            0-> false
            1->true
            else -> false

        }
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

        val deletedGroups : List<GroupDatabase> = it.filterNot {group->
            groupPhone.any{phoneGroup->
                phoneGroup.groupId == group.groupId && phoneGroup.groupName == group.groupName
            }
        }

        val newGroups : List<GroupDatabase> = groupPhone.filterNot { phoneGroup->
            it.any {  group->
                group.groupId==phoneGroup.groupId && group.groupName == phoneGroup.groupName
            }
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
        val k = profileSorted.map {
            ProfileItemData(it.profileId,it.profileName,Converters.getResourceInt(a,it.profileIcon),it.profileColor,false)
        }
        k.find { it.profileId==lastProfileId }?.profileChecked=true
        _profileList.value  = k
    }

    fun activateProfile(position: Int) {
        val profileId = profileSorted[position].profileId
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
        val editor = sharedPref.edit()
        editor.putLong("last_profile",profileId)
        editor.apply()
        Toast.makeText(a,"Profile activation completed",Toast.LENGTH_SHORT).show()
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

    fun updateProfileListVisibility(it: Boolean) {
        if (it)
        {
            val notificationManager = a.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            if (notificationManager != null && notificationManager.isNotificationPolicyAccessGranted) {
                notificationManager.setInterruptionFilter(INTERRUPTION_FILTER_PRIORITY)
            }
            _isProfileListVisible.value = View.VISIBLE
        }
        else
        {
            val notificationManager = a.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            if (notificationManager != null && notificationManager.isNotificationPolicyAccessGranted) {
                notificationManager.setInterruptionFilter(INTERRUPTION_FILTER_ALL)
            }
            _isProfileListVisible.value = View.GONE
            if (_profileList.value!=null){
                _profileList.value = _profileList.value?.map {
                    ProfileItemData(it.profileId,it.profileName,it.profileIcon,it.profileColor,false)
                }
            }
            removeFavorites()
            val editor = sharedPref.edit()
            editor.putLong("last_profile",-1L)
            editor.apply()
        }


    }

}