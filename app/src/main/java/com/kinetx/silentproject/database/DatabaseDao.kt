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

    @Query("SELECT * FROM profile_database WHERE profileId=:profileId")
    fun getProfileWithId(profileId: Long) : ProfileDatabase


    @Insert
    suspend fun insertGroup(profile: GroupDatabase)

    @Update
    suspend fun updateGroup(profile: GroupDatabase)

    @Delete
    suspend fun deleteGroup(profile: GroupDatabase)

    @Query("SELECT * FROM group_database")
    fun getAllGroups() : LiveData<List<GroupDatabase>>

}