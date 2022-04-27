package com.example.myreccomendation

import android.content.Context
import java.security.AccessControlContext
//data class SolutionArray() {
//
//}

class Solutions(context: Context) {
    private var _context:Context = context
    private var suggestions: ArrayList<String> = arrayListOf<String>("ischemia ad ","infection ad ", "both ad ", "neither ad ","call 911 ")
//    fun buildSolutions() {
////        _context.getString()
//    }
    fun getSolutions(choices: ArrayList<Int>): ArrayList<String> {
       var solutions =  arrayListOf<String>()
        solutions.add(suggestions[choices[0]])
        for (i in 1..5) {
            if(choices[i]==1&& !solutions.contains(suggestions[4])) {
                solutions.add(suggestions[4])
//                _context.getString(R.string.)
            }
        }

        return solutions

    }
}