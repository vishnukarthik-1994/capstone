package com.example.dfu_app.ui.analysis_record

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.dfu_app.R
import com.example.dfu_app.databinding.FragmentAnalysisRecordBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class AnalysisRecordFragment: Fragment() {
    private lateinit var viewModel: AnalysisRecordViewModel
    private lateinit var adapter: AnalysisRecordAdapter
    private val dp: FirebaseFirestore = Firebase.firestore
    private val users = dp.collection("users")
    private var userEmail:String= Firebase.auth.currentUser!!.email!!
    private var timeStamp:Long = 0
    private var _binding: FragmentAnalysisRecordBinding? = null
    private val binding get() = _binding!!
    private var ulcerType:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set callback
        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                val action = AnalysisRecordFragmentDirections.actionNavAnalysisRecordToNavHome()
                requireView().findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnalysisRecordBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AnalysisRecordViewModel::class.java]
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        viewModel.update()
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        adapter = AnalysisRecordAdapter()
        binding.verticalRecyclerView.adapter = AnalysisRecordAdapter()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawRecords()
        bind()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun bind( ) {
        binding.apply {
            infectionButton.setOnClickListener {
                infectionButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkBlue))
                bothButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
                ischemiaButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
                ulcerType = 1
                drawRecords()
            }
            ischemiaButton.setOnClickListener {
                infectionButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
                bothButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
                ischemiaButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkBlue))
                ulcerType = 2
                drawRecords()
            }
            bothButton.setOnClickListener {
                infectionButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
                bothButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkBlue))
                ischemiaButton.setTextColor(ContextCompat.getColor(requireContext(),R.color.grey))
                ulcerType = 3
                drawRecords()
            }
        }
    }
    private fun drawRecords() {
        val listRecords = ArrayList<Entry>()
        //read cloud file
        val user = users.document(userEmail).collection("record")
        user.get().addOnSuccessListener {documents ->
            //store date and number of ulcer in all records
            val listPair = mutableListOf<Pair<Long,Float>>()
            val cal = Calendar.getInstance()
            for (doc in documents){
                val source = doc.data["date"].toString()
                val date = source.substringBefore("_") + source.substringAfter("_")
                val count:String = when(ulcerType) {
                    0 -> doc.data["number"].toString()
                    1 -> doc.data["Infection"].toString()
                    2 -> doc.data["Ischemia"].toString()
                    else -> doc.data["Both"].toString()
                }

                // change string to local date type
                val originalFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.ENGLISH)
                val localDate = LocalDate.parse(date,originalFormat)
                //using calender to change localDate to milliseconds
                cal.clear()
                cal.set(localDate.year,localDate.monthValue-1,localDate.dayOfMonth)
                val milliSec = cal.timeInMillis
                //using milliseconds to avoid date calculation
                listPair.add(Pair(milliSec,count.toFloat()))
            }
            if (listPair.size != 0) {
                // sort all records by time
                // if records not sorted, Line chart don't know how to plot data
                listPair.sortBy { it.first }
                //find the earliest date
                timeStamp = listPair[0].first
                for (i in listPair.indices) {
                    //changing data type to feet the Line chart data format
                    listRecords.add(Entry((listPair[i].first - timeStamp).toFloat(),listPair[i].second))
                }
                setLineChartData(listRecords)
            }
        }.addOnFailureListener{
            Log.d(ContentValues.TAG, "Get cloud storage Error")
        }
    }
    private fun setLineChartData(sourceList: ArrayList<Entry>) {
        val label:String = when(ulcerType) {
            0 -> "Ulcer"
            1 -> "Infection"
            2 -> "Ischemia"
            else -> "Both"
        }
        val lineDataset = LineDataSet(sourceList, label)

        //set line color
        lineDataset.color = ContextCompat.getColor(requireContext(), R.color.darkBlue)
        lineDataset.circleRadius = 6f
        lineDataset.valueTextSize = 10F
        lineDataset.lineWidth = 1.5F
        //set circle color
        lineDataset.setCircleColor(ContextCompat.getColor(requireContext(), R.color.lightBlue))
        //no line fill
        lineDataset.setDrawFilled(false)
        //set circle style
        lineDataset.mode = LineDataSet.Mode.CUBIC_BEZIER

        //connect our data to the UI Screen
        val data = LineData(lineDataset)
        //Removed decimal in y axis
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(v: Float): String {
                val str = v.toString() + ""
                return if (str.isNotEmpty()) str.substring(0, str.indexOf(".")) else str
            }
        })
        binding.getTheGraph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        // Changing x axis to MMM DD format
        binding.getTheGraph.xAxis.valueFormatter = XAxisValueFormatter()
        binding.getTheGraph.xAxis.setDrawGridLines(false)
        binding.getTheGraph.axisLeft.setDrawGridLines(false)
        binding.getTheGraph.axisRight.setDrawGridLines(false)
        binding.getTheGraph.axisLeft.setDrawAxisLine(false)
        binding.getTheGraph.axisRight.setDrawAxisLine(false)
        binding.getTheGraph.description.isEnabled = false
        binding.getTheGraph.data = data
        binding.getTheGraph.setBackgroundColor(Color.TRANSPARENT)
        binding.getTheGraph.axisRight.setDrawLabels(false)
        binding.getTheGraph.axisLeft.setDrawLabels(false)
        binding.getTheGraph.xAxis.textSize = 15F
        binding.getTheGraph.xAxis.axisLineWidth = 1.5F
        binding.getTheGraph.xAxis.axisLineColor = ContextCompat.getColor(requireContext(), R.color.darkBlue)
        binding.getTheGraph.xAxis.granularity = 1F
        binding.getTheGraph.xAxis.labelCount = sourceList.size
        binding.getTheGraph.extraBottomOffset = 20F
        binding.getTheGraph.extraLeftOffset = 25F
        binding.getTheGraph.extraRightOffset = 25F
        binding.getTheGraph.legend.textSize = 15F
        binding.getTheGraph.setBorderColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.getTheGraph.animateXY(2000, 2000, Easing.EaseInOutCubic)
    }
    inner class XAxisValueFormatter: ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            var millis = (value  + timeStamp).toLong()
            val formatter = SimpleDateFormat("MMM dd")
            return formatter.format(millis)
        }
    }
}