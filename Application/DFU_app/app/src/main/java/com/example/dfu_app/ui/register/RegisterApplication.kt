package com.example.dfu_app.ui.register

import android.app.Application
import com.example.dfu_app.data.UserInfoDatabase

class RegisterApplication: Application() {
    val userInfoDatabase: UserInfoDatabase by lazy { UserInfoDatabase.getDatabase(this) }
}