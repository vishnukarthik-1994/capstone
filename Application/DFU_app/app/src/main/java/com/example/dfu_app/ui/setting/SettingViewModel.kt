package com.example.dfu_app.ui.setting

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.dfu_app.imageprocessor.ImagePreprocessing
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.Path

class SettingViewModel: ViewModel() {
    private lateinit var _user:String
    private val dp: FirebaseFirestore = Firebase.firestore
    private val users = dp.collection("users")
    private var absolutePath:String = ImagePreprocessing.absolutePath
    private val storage = Firebase.storage
    fun getUser(user:String){
        _user = user
    }
    fun removeRecords(){
        val records = users.document(_user).collection("record")
        val recordName = mutableListOf<String>()
        records.get().addOnSuccessListener { result ->
            for (doc in result){
                recordName.add(doc.id)
            }
            remove(recordName)
        }
    }
    private fun remove(recordName:MutableList<String>){
        for (record in recordName) {
            //remove records in cloud db
            users.document(_user).collection("record").document(record).delete()
            //remove records in cloud storage
            val protectString = "_"
            val fileRef = storage.reference.child("$_user/$_user$protectString$record.png")
            fileRef.delete().addOnSuccessListener {
                Log.d(ContentValues.TAG, "Remove cloud $fileRef successfully")
            }.addOnFailureListener {
                Log.d(ContentValues.TAG, "Remove cloud $fileRef failed")
            }
            //remove records in cloud local file
            val path = Path("$absolutePath/$_user$protectString$record.png")
            try {
                if (Files.deleteIfExists(path)) {
                    Log.d(ContentValues.TAG, "Remove local $fileRef successfully")
                } else {
                    Log.d(ContentValues.TAG, "Remove local $fileRef failed")
                }
            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}