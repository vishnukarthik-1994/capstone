package com.example.dfu_app.ui.home

import android.content.ContentValues
import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import com.example.dfu_app.LoginActivity
import com.example.dfu_app.R
import com.example.dfu_app.databinding.FragmentHomeBinding
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


class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val dp: FirebaseFirestore = Firebase.firestore
    private val users = dp.collection("users")
    private var userEmail:String= Firebase.auth.currentUser!!.email!!
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var timeStamp:Long = 0
//    private var timeStamp:Float = 0F
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                val action = HomeFragmentDirections.actionNavHomeSelf()
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
        //remove previous view
        container?.removeAllViews()
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawRecords()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                val count = doc.data["number"].toString()
                // change string to local date type
                val originalFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss",Locale.ENGLISH)
                val localDate = LocalDate.parse(date,originalFormat)
                //using calender to change localDate to milliseconds
                cal.clear()
                cal.set(localDate.year,localDate.monthValue-1,localDate.dayOfMonth)
                val milliSec = cal.timeInMillis
                //using milliseconds to avoid date calculation
                listPair.add(Pair(milliSec,count.toFloat()))
            }

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
        }.addOnFailureListener{
            Log.d(ContentValues.TAG, "Get cloud storage Error")
        }
    }
    private fun setLineChartData(sourceList:ArrayList<Entry>) {

        val lineDataset = LineDataSet(sourceList, "Ulcer")

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
        binding.getTheGraph.xAxis.axisLineColor = ContextCompat.getColor(requireContext(), R.color.white)
        binding.getTheGraph.xAxis.granularity = 1F
        binding.getTheGraph.xAxis.labelCount = sourceList.size
        binding.getTheGraph.extraBottomOffset = 20F
        binding.getTheGraph.extraLeftOffset = 25F
        binding.getTheGraph.setBorderColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.getTheGraph.animateXY(2000, 2000, Easing.EaseInOutCubic)
    }
    inner class XAxisValueFormatter:ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            var millis = (value  + timeStamp).toLong()
            val formatter = SimpleDateFormat("MMM dd")
            return formatter.format(millis)
        }
    }
}



