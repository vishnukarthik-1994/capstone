package com.example.dfu_app.ui.error_message

import android.content.Context
import android.view.Gravity
import android.widget.Toast

class ErrorMessage {
    fun setErrorMessage(context: Context,Str: String){
        val duration  = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context,Str,duration)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()
    }
}