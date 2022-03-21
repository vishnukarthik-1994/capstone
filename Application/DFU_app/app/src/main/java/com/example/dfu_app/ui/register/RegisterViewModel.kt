package com.example.dfu_app.ui.register

import androidx.lifecycle.*
import com.example.dfu_app.data.UserInfo
import com.example.dfu_app.data.UserInfoDao
import kotlinx.coroutines.launch

class RegisterViewModel(private val userInfoDao: UserInfoDao): ViewModel() {
    lateinit var allUserName: List<String>
    lateinit var allUserInfo: List<UserInfo>
    lateinit var userInfo: UserInfo
    private lateinit var _accountName:String
    private lateinit var _firstName:String
    private lateinit var _lastName:String
    private lateinit var _password: String
    private var _height:Double = 0.0
    private var _weight:Double = 0.0
    private var _age:Int = 0
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
    fun checkUserName(userName:String):Boolean{
        return allUserName.contains(userName)
    }
    fun checkInput(source:String):Boolean{
        return source.isNotBlank()
    }
    fun setName(firstname: String,lastname: String,accountName:String){
        _firstName = firstname
        _lastName = lastname
        _accountName = accountName
    }
    fun setPassword(password: String){
        _password = password
    }
    fun setInfo(height: String,weight: String,age: String){
        _height = height.toDouble()
        _weight = weight.toDouble()
        _age = age.toInt()
    }
    fun createAccount(){
        val account = UserInfo(
            userName = _accountName,
            password = _password,
            firstname = _firstName,
            lastname = _lastName,
            weight = _weight,
            height = _height,
            age = _age
        )
        insertAccount(account)
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