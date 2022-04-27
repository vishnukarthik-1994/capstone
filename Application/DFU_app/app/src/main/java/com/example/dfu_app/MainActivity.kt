package com.example.dfu_app


import android.os.Bundle
import android.os.Environment
import android.text.TextUtils.replace
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.dfu_app.databinding.ActivityMainBinding
import com.example.dfu_app.imageprocessor.ImagePreprocessing
import com.example.dfu_app.ui.analysis_record.AnalysisRecordFragment
import com.example.dfu_app.ui.daily_survey.DailySurveyFragment
import com.example.dfu_app.ui.error_message.ErrorMessage
import com.example.dfu_app.ui.home.HomeFragment
import com.example.dfu_app.ui.home.HomeFragmentDirections
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var userEmail:String
    private  var userName:String = "0"
    private lateinit var barName:TextView
    private lateinit var navController: NavController
    private val dp: FirebaseFirestore = Firebase.firestore
    private val users = dp.collection("users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImagePreprocessing.absolutePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setSupportActionBar(binding.appBarMain.toolbar)
        //Detect sign out action
        userEmail = intent.getStringExtra("user")!!
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val tts  = applicationContext
        getUserName()
        if(navView.headerCount > 0){
            //Set email info
            val header = navView.getHeaderView(0)
            val barEmail = header.findViewById<TextView>(R.id.barGmail)
            barName = header.findViewById<TextView>(R.id.barFirstname)
            barEmail.text = userEmail
        }

        navController = findNavController(R.id.nav_host_fragment_content_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_bottom_menu)
        bottomNavigationView.setupWithNavController(navController).apply {
            navController.navigateUp()
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(R.id.nav_home), drawerLayout
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
    }
    private fun getUserName(){
        val user = users.document(userEmail)
        user.get().addOnSuccessListener {
            userName = it.get("firstName").toString()
            barName.text = userName
        }
    }




    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
