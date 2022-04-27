package com.example.dfu_app.ui.register

import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
//
class RegisterViewModel: ViewModel() {

    private lateinit var _user:String
    private lateinit var _firstName:String
    private lateinit var _lastName:String
    private var _height:Double = 0.0
    private var _weight:Double = 0.0
    private var _age:Int = 0
    private val dp:FirebaseFirestore = Firebase.firestore
    private val users = dp.collection("users")

    fun getUser(user:String){
        _user = user
    }
    fun checkInput(source:String):Boolean{
        return source.isNotBlank()
    }
    fun setName(firstname: String,lastname: String){
        _firstName = firstname
        _lastName = lastname
    }
    fun setInfo(height: String,weight: String,age: String){
        _height = height.toDouble()
        _weight = weight.toDouble()
        _age = age.toInt()
    }
    fun uploadUserToDp(){
        val userInfo = hashMapOf(
            "user" to _user,
            "firstName" to _firstName,
            "lastName" to _lastName,
            "age" to _age,
            "weight" to _weight,
            "height" to _height
        )
        users.document(_user).set(userInfo)
    }
}
class RegisterViewModelFactory() : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}