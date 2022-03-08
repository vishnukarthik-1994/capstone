package com.example.dfu_app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserInfoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userInfo: UserInfo)
    @Query("Select username From Account")
    suspend fun getUserName():List<String>
    @Query("Select * From Account")
    suspend fun getUserInfo():List<UserInfo>
}