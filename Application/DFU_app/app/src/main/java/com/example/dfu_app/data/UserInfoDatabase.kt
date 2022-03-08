package com.example.dfu_app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserInfo::class], version = 3, exportSchema = false)
abstract class UserInfoDatabase: RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDao
    companion object {
        @Volatile
        private var INSTANCE: UserInfoDatabase? = null
        fun getDatabase(context: Context): UserInfoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserInfoDatabase::class.java,
                    "item_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}