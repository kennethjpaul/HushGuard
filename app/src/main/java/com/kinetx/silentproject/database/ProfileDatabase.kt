package com.kinetx.silentproject.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_database")
data class ProfileDatabase(

    @PrimaryKey(autoGenerate = true)
    var profileId : Long = -1L,

    @ColumnInfo(name = "profile_name")
    var profileName : String = "",

    @ColumnInfo(name = "profile_icon")
    var profileIcon : String = "guitar",

    @ColumnInfo(name ="profile_color")
    var profileColor : Int = -1,

    @ColumnInfo(name="group_ids")
    var groupIds : List<Long> = emptyList<Long>()
)
