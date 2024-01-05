package com.kinetx.silentproject.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_database")
data class GroupDatabase(
    @PrimaryKey()
    var groupId : Long = -1L,

    @ColumnInfo(name = "group_name")
    var groupName : String = ""
)
