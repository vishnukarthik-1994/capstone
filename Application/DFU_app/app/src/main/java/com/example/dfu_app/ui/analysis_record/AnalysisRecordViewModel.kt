package com.example.dfu_app.ui.analysis_record


import android.content.ContentValues
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dfu_app.data.DiagnosisPhoto
import com.example.dfu_app.imageprocessor.ImagePreprocessing
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File


class AnalysisRecordViewModel: ViewModel(){
    private val storage = Firebase.storage
    private var userEmail:String= Firebase.auth.currentUser!!.email!!
    private var absolutePath:String = ImagePreprocessing.absolutePath
    private val dp: FirebaseFirestore = Firebase.firestore
    private val users = dp.collection("users")
    private val _status = MutableLiveData<Boolean>()
    // The external immutable LiveData for the record status if true the record existed
    val status: LiveData<Boolean> = _status
    // Internally, we use a MutableLiveData, because we will be updating the List of Records
    // The external LiveData interface to the property is immutable, so only this class can modify
    private val _records  = MutableLiveData<MutableList<DiagnosisPhoto>>()
    var records: LiveData<MutableList<DiagnosisPhoto>> = _records
    fun update(){
        _records.value = mutableListOf()
        _status.value = false
        val localImages = getLocalFile()
        //read cloud file
        val user = users.document(userEmail).collection("record")
        val updateList = mutableListOf<Pair<String,QueryDocumentSnapshot>>()
        user.get().addOnSuccessListener {documents ->
            for (doc in documents){
                val cloudImg = doc.data["path"].toString()
                if (cloudImg !in localImages){
                    updateList.add(Pair(cloudImg,doc))
                }
                else {
//                    val file = File("$absolutePath/$cloudImg")
                    val record = DiagnosisPhoto(cloudImg,"$absolutePath/$cloudImg".toUri(),separateCount(doc),doc["suggestion"].toString())
//                    val record = DiagnosisPhoto(cloudImg,file.toUri())
                    _records.addNewItem(record)
                    _status.value = true
                }
            }
            //Record is previous version
            if (updateList.size != 0){
                downLoad(updateList)
            }
        }.addOnFailureListener{
            Log.d(ContentValues.TAG, "Get cloud storage Error")
        }
    }
    private fun separateCount(doc: QueryDocumentSnapshot): String {
        var msg = "Found:"
        val map =  hashMapOf(
            "Both" to doc["Both"].toString().toInt(),
            "Infection" to doc["Infection"].toString().toInt(),
            "Ischemia" to doc["Ischemia"].toString().toInt(),
            "None" to doc["None"].toString().toInt()
        )
        var ulcerFound = false
        for (key in map.keys) {
            val count = map[key]
            if (count != 0) {
                msg += " $count $key "
                ulcerFound = true
            }
        }
        if (!ulcerFound) {
            msg = "No ulcer found"
        }
        var footTemp =  doc["FootTemperature"]
        if (footTemp != null) {
            msg += "\nFootTemperature: $footTemp F"
        }
        return msg
    }
    private fun getLocalFile(): MutableSet<String>{
        val dir  = File(absolutePath)
        val localImages = mutableSetOf<String>()
        val files = dir.listFiles()!!
        //read all local files
        for (file in files){
            if (checkImg(file.absolutePath.toString())){
                val fileName = file.absolutePath.toString().split("/").last()
                val nameUser = fileName.split("_").first()
                if (nameUser == userEmail){
                    localImages.add(fileName)
//                    val record = DiagnosisPhoto(fileName,file.toUri())
//                    _records.addNewItem(record)
//                    _status.value = true
                }
            }
        }
        return localImages
    }

    private fun downLoad(toDoList:List<Pair<String,QueryDocumentSnapshot>>){
        for ((cloudImg,doc) in toDoList ){
            val file = File("$absolutePath/$cloudImg")
            val imageRef = storage.reference.child("$userEmail/$cloudImg")
            imageRef.getFile(file).addOnSuccessListener {
                val record = DiagnosisPhoto(cloudImg,"$absolutePath/$cloudImg".toUri(),separateCount(doc),doc["suggestion"].toString())
                _records.addNewItem(record)
                _status.value = true
                Log.d(ContentValues.TAG, "Download Images Successful")
            }.addOnFailureListener{
                Log.d(ContentValues.TAG, "Download Images Error")
            }
        }
    }
    private fun checkImg(imgPath:String):Boolean{
        val extension = imgPath.substring(imgPath.lastIndexOf(".")+1,imgPath.length).lowercase()
        return extension == "jpg" || extension == "png"
    }
    private fun <DiagnosisPhoto> MutableLiveData<MutableList<DiagnosisPhoto>>.addNewItem(item: DiagnosisPhoto) {
        val oldValue = this.value ?: mutableListOf()
        oldValue.add(item)
        this.value = oldValue
    }
}

