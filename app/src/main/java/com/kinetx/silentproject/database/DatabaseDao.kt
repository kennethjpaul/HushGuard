package com.kinetx.silentproject.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DatabaseDao {

    @Insert
    suspend fun insertProfile(profile: ProfileDatabase)

    @Update
    suspend fun updateProfile(profile: ProfileDatabase)

    @Delete
    suspend fun deleteProfile(profile: ProfileDatabase)

    @Query("SELECT * FROM profile_database")
    fun getAllProfiles() : LiveData<List<ProfileDatabase>>

}