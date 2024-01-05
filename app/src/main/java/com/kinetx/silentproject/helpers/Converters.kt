package com.kinetx.silentproject.helpers

import android.app.Application
import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Converters {

    @TypeConverter
    fun listToJson(value: List<Long>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<Long>>(value)

    fun getResourceInt(application: Application, fileName : String) : Int
    {
        val c = application.applicationContext
        val b  =c.resources.getIdentifier(fileName,"drawable",c.packageName)

        return b
    }
}