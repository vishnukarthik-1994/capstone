package com.example.dfu_app.data
import android.net.Uri

data class DiagnosisPhoto (
    val id: String,
    val uri: Uri,
    val predict: HashMap<String, String>,
    val recommendation: String
)
