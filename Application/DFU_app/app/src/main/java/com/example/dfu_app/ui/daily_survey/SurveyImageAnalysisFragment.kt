package com.example.dfu_app.ui.daily_survey

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.dfu_app.R
import com.example.dfu_app.databinding.FragmentSurveyImageAnalysisBinding
import com.example.dfu_app.imageprocessor.ImagePreprocessing
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.IOException
import java.util.*

class SurveyImageAnalysisFragment: Fragment() {

    private val shareViewModel: SurveyViewModel by activityViewModels()
    private val CAMERA_PERM_CODE = 101
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentPhotoPath: String
    private var _binding: FragmentSurveyImageAnalysisBinding? = null
    private lateinit var photoURI: Uri
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var timeStamp:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set callback
        val callback = object : OnBackPressedCallback(true /** true means that the callback is enabled */) {
            override fun handleOnBackPressed() {
                val action = SurveyImageAnalysisFragmentDirections.actionNavSurveyImageAnalysisToNavDailySurveyStart()
                requireView().findNavController().navigate(action)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //remove previous view
        container?.removeAllViews()
        _binding = FragmentSurveyImageAnalysisBinding.inflate(inflater, container, false)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    binding.footImage.setImageBitmap(ImagePreprocessing.loadingImg( currentPhotoPath ))
                    shareViewModel.prediction(ImagePreprocessing.loadingImg( currentPhotoPath ),currentPhotoPath, timeStamp)
                }
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
        shareViewModel.loadingModel(requireContext().assets)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun bind( ) {
        binding.apply {
            submitDailSurveyButton.setOnClickListener { next() }
            cameraButton.setOnClickListener{ askCameraPermissions() }
//            cameraButton.setOnClickListener{ test() }
            requireView().setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (requireActivity().currentFocus != null && requireActivity().currentFocus!!.windowToken != null) {
                        closeKeyBoards()
                 }
            }
            false
            }
        }
    }
    private fun askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), CAMERA_PERM_CODE)
            Log.e(TAG, "askCameraPermissions: " )
        } else {
            openCamera()
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val userEmail = Firebase.auth.currentUser!!.email!!
        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File(
            "${storageDir}/${userEmail}_${timeStamp}.png"
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
    private fun openCamera() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException)
        {
            null
        }
        photoFile?.also {  photoURI =
                            FileProvider.getUriForFile(requireContext(), "com.example.android.fileprovider", it) }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)
        resultLauncher.launch(intent)
        }
    private fun test(){
        val testImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.footulcer2), ImagePreprocessing.INPUT_WIDTH, ImagePreprocessing.INPUT_HEIGHT, true)
        try {
            createImageFile()
        } catch (ex: IOException)
        {
            Log.d(TAG, "Create Fail failed")
        }
        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        shareViewModel.prediction(testImage,currentPhotoPath,timeStamp)
    }

    private fun next(){
        val action = SurveyImageAnalysisFragmentDirections.actionNavSurveyImageAnalysisToNavSurveyQuestionnaire()
        this.findNavController().navigate(action)
    }
    private fun closeKeyBoards(){
        val manager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(
            requireActivity().currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}