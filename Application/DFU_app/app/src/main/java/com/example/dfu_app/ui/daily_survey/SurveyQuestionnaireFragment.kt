package com.example.dfu_app.ui.daily_survey

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.dfu_app.R
import com.example.dfu_app.data.Solutions
import com.example.dfu_app.databinding.FragmentSurveyQuestionnaireBinding
import com.example.dfu_app.ui.error_message.ErrorMessage.setMessage

class SurveyQuestionnaireFragment:Fragment() {
    private val viewModel: SurveyViewModel by activityViewModels()
    private var _binding: FragmentSurveyQuestionnaireBinding? = null
    private var mCurrentPosition: Int = 0
    private var check = 0
    private val question: ArrayList<String> = arrayListOf()
    private var mChoices: ArrayList<Int> = arrayListOf()
    private var recommendations: ArrayList<String> = arrayListOf()
    private lateinit var solution: Solutions
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //remove previous view
        container?.removeAllViews()
        _binding = FragmentSurveyQuestionnaireBinding.inflate(inflater, container, false)
        solution = Solutions()
        getQuestion()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun bind( ) {
        binding.apply {
            btnSubmit.setOnClickListener {
                check = when(chooseOptions.checkedRadioButtonId) {
                    R.id.option_no -> -1
                    R.id.option_yes -> 1
                    else ->0
                }
                if (check != 0) {
                    mChoices.add(check)
                    check = 0
                    mCurrentPosition += 1
                    // check if the last question
                    if (mCurrentPosition < 5) setQuestion()
                    else {
                        //generate the recommendation
                        recommendations = solution.getSolutions(mChoices)
                        viewModel.setRecommendation(recommendations)
                        navigate()
                    }
                    optionNo.isChecked = false
                    optionYes.isChecked = false
                    chooseOptions.clearCheck()
                }
            }
        }
    }
    private fun navigate(){
        val action = SurveyQuestionnaireFragmentDirections.actionNavSurveyQuestionnaireToNavAnalysisRecord()
        requireView().findNavController().navigate(action)
    }
    private fun getQuestion() {
        question.add(getString(R.string.q2))
        question.add(getString(R.string.q3))
        question.add(getString(R.string.q4))
        question.add(getString(R.string.q5))
    }
    @SuppressLint("SetTextI18n")
    private fun setQuestion() {
        val strQuestion = question[mCurrentPosition-1]
        binding.progressBar.progress += 1
        val next = mCurrentPosition+1
        binding.tvProgress.text = "$next" + "/" + binding.progressBar.max
        binding.tvQuestion.text = strQuestion
    }
}