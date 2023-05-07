package com.kinetx.silentproject.database

import androidx.lifecycle.LiveData

class DatabaseRepository(private val databaseDao: DatabaseDao) {

    val getAllProfiles : LiveData<List<ProfileDatabase>> = databaseDao.getAllProfiles()


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

}