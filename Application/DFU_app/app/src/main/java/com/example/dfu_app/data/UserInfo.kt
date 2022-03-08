package com.example.dfu_app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Account")
data class UserInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "username")
    val userName: String?,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "firstname")
    val firstname: String,
    @ColumnInfo(name = "lastname")
    val lastname: String,
    @ColumnInfo(name = "weight")
    val weight: Double,
    @ColumnInfo(name = "height")
    val height: Double,
    @ColumnInfo(name = "age")
    val age: Int
)
