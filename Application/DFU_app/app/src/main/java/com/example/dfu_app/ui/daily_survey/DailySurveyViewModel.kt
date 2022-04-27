package com.example.dfu_app.ui.daily_survey

import android.content.ContentValues
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dfu_app.model.PytorchPrediction
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.io.*

class DailySurveyViewModel: ViewModel() {
    private val storage = Firebase.storage
    private lateinit var model:PytorchPrediction

    private lateinit var storageRef: StorageReference
    private val dp: FirebaseFirestore = Firebase.firestore
    private val users = dp.collection("users")
    private val userEmail = Firebase.auth.currentUser!!.email!!
    fun loadingModel(assetManager: AssetManager){
        try {
            model = PytorchPrediction(assetManager)
        }
        catch(e:FileNotFoundException){
            e.printStackTrace()
        }
    }
    fun prediction(source:Bitmap,path:String, timeStamp:String){
        viewModelScope.launch {
            try{
                val (predictBitmap,count) = model.modelPredict(source)
                predictBitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(path))
                val stream:InputStream = FileInputStream(File(path))
                val fileName = path.split("/").last()
                //update dp
                val route = hashMapOf(
                    "path" to fileName,
                    "number" to count,
                    "date" to timeStamp
                )
                users.document(userEmail).collection("record").document(timeStamp).set(route)
                //upload image
                storageRef = storage.reference.child("$userEmail/$fileName" )
                val uploadTask =storageRef.putStream(stream)
                uploadTask.addOnFailureListener{
                    Log.d(ContentValues.TAG, "Upload Image:success")
                }.addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Upload Image:Fail")
                }
            }
            catch(e: Exception){
                e.printStackTrace()
            }
        }
    }
}