package com.example.dfu_app.ui.error_message

import android.content.Context
import android.view.Gravity
import android.widget.Toast

object ErrorMessage {
    fun setErrorMessage(context: Context,Str: String){
        val duration  = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context,Str,duration)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()
    }
    fun setMessage(context: Context,Str: String){
        val duration  = Toast.LENGTH_LONG
        val toast = Toast.makeText(context,Str,duration)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.show()
    }
}