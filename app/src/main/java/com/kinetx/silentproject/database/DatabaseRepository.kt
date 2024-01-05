package com.kinetx.silentproject.database

import androidx.lifecycle.LiveData

class DatabaseRepository(private val databaseDao: DatabaseDao) {

    val getAllProfiles : LiveData<List<ProfileDatabase>> = databaseDao.getAllProfiles()
    val getAllGroups : LiveData<List<GroupDatabase>> = databaseDao.getAllGroups()


    suspend fun insertProfile(profile: ProfileDatabase)
    {
        databaseDao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: ProfileDatabase)
    {
        databaseDao.updateProfile(profile)
    }

    suspend fun deleteProfile(profile: ProfileDatabase)
    {
        databaseDao.deleteProfile(profile)
    }


    suspend fun insertGroup(group: GroupDatabase)
    {
        databaseDao.insertGroup(group)
    }

    suspend fun updateGroup(group: GroupDatabase)
    {
        databaseDao.updateGroup(group)
    }

    suspend fun deleteGroup(group: GroupDatabase)
    {
        databaseDao.deleteGroup(group)
    }

    fun getProfileWithId(profileId: Long): ProfileDatabase
    {
        return databaseDao.getProfileWithId(profileId)
    }

}