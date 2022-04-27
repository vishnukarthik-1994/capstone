package com.example.dfu_app.ui.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dfu_app.LoginActivity
import com.example.dfu_app.databinding.FragmentSettingBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SettingFragment: Fragment() {
    private lateinit var viewModel:SettingViewModel
    private var _binding: FragmentSettingBinding? = null
    private val dp: FirebaseFirestore = Firebase.firestore
    private val users = dp.collection("users")
    private val userEmail = Firebase.auth.currentUser!!.email
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
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        getUserInfo()
        viewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        viewModel.getUser(userEmail!!)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun bind( ) {
        binding.apply {
            signOutButton.setOnClickListener { logout() }
            removeDataButton.setOnClickListener { viewModel.removeRecords() }
            }
        }
    private fun logout(){
        val intent = Intent().setClass(requireActivity(), LoginActivity::class.java)
        intent.putExtra("signOut",true)
        startActivity(intent)
    }
    private fun getUserInfo(){
        val user = users.document(userEmail!!)
        user.get().addOnSuccessListener {
            binding.apply {
                val fullName = "${it.get("firstName").toString()}_${it.get("lastName").toString()}"
                name.text = fullName
                ageText.text = it.get("age").toString()
                heightText.text  = it.get("height").toString()
                weightText.text = it.get("weight").toString()
                barGmail.text = userEmail
            }
        }
    }
}