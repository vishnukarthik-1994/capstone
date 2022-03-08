package com.example.dfu_app.ui.register

import androidx.lifecycle.*
import com.example.dfu_app.data.UserInfo
import com.example.dfu_app.data.UserInfoDao
import kotlinx.coroutines.launch

class RegisterViewModel(private val userInfoDao: UserInfoDao): ViewModel() {
    lateinit var allUserName: List<String>
    lateinit var allUserInfo: List<UserInfo>
    lateinit var userInfo: UserInfo
    private fun insertAccount(userInfo: UserInfo){
        viewModelScope.launch {
            userInfoDao.insert(userInfo)
        }
    }
    fun getUserName(){
        viewModelScope.launch {
            allUserName = userInfoDao.getUserName()
        }
    }
    fun getUserInfo(){
        viewModelScope.launch {
            allUserInfo = userInfoDao.getUserInfo()
        }
    }
    fun checkLogin(userName: String,password: String):Boolean{
        for (user in allUserInfo){
            if (userName.equals(user.userName,true)
                && password.equals(user.password,true) )
            {
                userInfo = user
                return true
            }
        }
        return false
    }
    private fun getNewAccountEntry(userName: String, password: String, firstname: String,
                                   lastname: String,weight: String, height: String, age: String): UserInfo
    {
        return UserInfo(
            userName = userName,
            password = password,
            firstname = firstname,
            lastname = lastname,
            weight = weight.toDouble(),
            height = height.toDouble(),
            age = age.toInt()
        )
    }
    fun checkUserName(userName:String):Boolean{
        return allUserName.contains(userName)
    }
    fun createAccount(userName: String, password: String, firstname: String,
                      lastname: String,weight: String, height: String, age: String){
        val account = getNewAccountEntry(userName,password,firstname,lastname,weight,height,age)
        insertAccount(account)
    }
    fun isStringValid(userName: String, password: String, firstname: String,
                      lastname: String,weight: String, height: String, age: String
    ): Boolean {
        if (userName.isBlank() || password.isBlank() || firstname.isBlank() || lastname.isBlank()
            || weight.isBlank() || height.isBlank() || age.isBlank() ) {
            return false
        }
        return true
    }

}
class RegisterViewModelFactory(private val userInfoDao: UserInfoDao) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(userInfoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}