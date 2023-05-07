package com.kinetx.silentproject.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kinetx.silentproject.helpers.Converters

@Database(entities = [ProfileDatabase::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DatabaseMain  : RoomDatabase()
{
    abstract val databaseDao : DatabaseDao

    companion object
    {

        @Volatile
        private var INSTANCE : DatabaseMain? = null

        fun getInstance(context: Context): DatabaseMain
        {
            synchronized(this)
            {
                var instance = INSTANCE

                if(instance==null)
                {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseMain::class.java,
                        "main_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }



}