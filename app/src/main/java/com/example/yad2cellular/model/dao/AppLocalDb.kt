package com.example.yad2cellular.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.yad2cellular.base.MyApplication
import com.example.yad2cellular.model.Post

@Database(entities = [Post::class], version =1)
abstract class AppLocalDbRepository: RoomDatabase() {
    abstract fun postDao(): PostDao
}

class AppLocalDb {
    val database: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.context ?: throw IllegalStateException("Application context is missing")

        Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}