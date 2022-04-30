package com.example.myreccomendation

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.myreccomendation.databinding.ActivitySurveyBinding
import kotlinx.android.synthetic.main.activity_survey.*
import kotlin.collections.ArrayList

class Survey : AppCompatActivity(){
    private var mCurrentPosition: Int = 2
    private var mQuestionList: ArrayList<Question>? = null
    private var mSelectedOptionPosition: Int = 0
    private var check = 0
    private val question: ArrayList<String> = arrayListOf<String>()
    private var mChoices: ArrayList<Int> = arrayListOf<Int>()
    private var recommendations: ArrayList<ArrayList<String>> = arrayListOf<ArrayList<String>>()
    private lateinit var binding:ActivitySurveyBinding
    private lateinit var solution:Solutions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getQuestion()
        setContentView(R.layout.activity_survey)
        mChoices.add(1) // 1: ischemia; 2: infection; 3: Both; 4: Neither
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        mQuestionList = Constants.getQuestions()
        solution = Solutions(applicationContext)
        bing()
    }
    private fun getQuestion() {
        question.add(getString(R.string.q1))
        question.add(getString(R.string.q2))
        question.add(getString(R.string.q3))
        question.add(getString(R.string.q4))
        question.add(getString(R.string.q5))
    }
    private fun bing() {
        binding.apply {
            btn_submit.setOnClickListener {
                check = when(choise_options.checkedRadioButtonId) {
                    R.id.option_no -> -1
                    R.id.option_yes -> 1
                    else ->0
                }
                if (check != 0) {
                    mChoices.add(check)
                    check = 0
                    // check if the last question
                    if (mCurrentPosition < 6)
                    {
                        setQuestion()
                    }
                    else {
                        recommendations = solution.getSolutions(mChoices)
//                        Log.d(recommendations.toString())
                        //generate the recommendation
                    }
                    mCurrentPosition += 1
                    option_no.isChecked = false
                    option_yes.isChecked = false
                    choise_options.clearCheck()
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setQuestion() {
        val strQuestion = question!!.get(mCurrentPosition-1)
//        defaultOptionsView()

//        if(mCurrentPosition == mQuestionList!!.size){
//            btn_submit.text= "Finish"
//        }
//        else{
//            btn_submit.text= "Submit"
//        }
        progressBar.progress= mCurrentPosition
//        progressBar.setIndeterminate(false)
        tv_progress.text = "$mCurrentPosition" + "/" + progressBar.max
        tv_question.text = strQuestion
    }

//    private fun defaultOptionsView() {
//        val options = ArrayList<TextView> ()
//        options.add(0, tv_first_choice)
//        options.add(1, tv_sec_choice)
//
//        for(option in options) {
//            option.setTextColor(Color.parseColor("#7A8089"))
//            option.typeface = Typeface.DEFAULT
//
//        }
//    }
//
//    private fun selectedOptionView(tv:TextView, selectedOptionNum: Int) {
//        defaultOptionsView()
//        mSelectedOptionPosition = selectedOptionNum
//        mChoices!!.add(selectedOptionNum)
//        tv.setTextColor(Color.parseColor(("#363A43")))
//        tv.setTypeface(tv.typeface, Typeface.BOLD)
//    }


}