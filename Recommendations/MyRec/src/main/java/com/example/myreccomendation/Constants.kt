package com.example.myreccomendation

import androidx.core.content.res.TypedArrayUtils.*
import com.example.myreccomendation.R.string.*

object Constants {
    fun getQuestions(): ArrayList<Question>{
        val questionList = ArrayList<Question>()
        val question1 = Question(
            1,
            "What is your type of ulcer",
            "ischemia",
            "infection",
            "both",
            "none",
        )
        questionList.add(question1)

        val question2 = Question(
            2,
            "Do you have acute onset of pain or increasing pain?",
            "Yes",
            "No",
            "",
            "",
        )
        questionList.add(question2)

        val question3 = Question(
            3,
            "Does your wound probes to bone?",
            "Yes",
            "No",
            "",
            "",
        )
        questionList.add(question3)


        val question4 = Question(
            4,
            "Has your gangrene develops or worsens in the past 14 days?",
            "Yes",
            "No",
            "",
            "",
        )
        questionList.add(question4)

        val question5 = Question(
            5,
            "Previously palpable peripheral pulses are diminished or absent?",
            "Yes",
            "No",
            "",
            "",
        )
        questionList.add(question5)

        val question6 = Question(
            6,
            "Has your systemic infection develops?",
            "Yes",
            "No",
            "",
            "",
        )
        questionList.add(question6)
        return questionList
    }
}