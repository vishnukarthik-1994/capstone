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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dfu_app.R
import com.example.dfu_app.databinding.FragmentDailySurveyBinding
import com.example.dfu_app.imageprocessor.ImagePreprocessing
import java.io.File
import java.io.IOException
import java.util.*


class DailySurveyFragment: Fragment() {
    private lateinit var viewModel: DailySurveyViewModel
    private val CAMERA_PERM_CODE = 101
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var currentPhotoPath: String
    private var _binding: FragmentDailySurveyBinding? = null
    private lateinit var photoURI: Uri
    private lateinit var Path: String
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDailySurveyBinding.inflate(inflater, container, false)
        ViewModelProvider(this)[DailySurveyViewModel::class.java].also { viewModel = it }
        viewModel.loadingModel(requireContext().assets)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    binding.footImage.setImageBitmap(ImagePreprocessing.loadingImg( Path ))
                    viewModel.prediction(ImagePreprocessing.loadingImg( Path ),Path)
                }
            }
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
            submitDailSurveyButton.setOnClickListener { submit() }
            cameraButton.setOnClickListener{ askCameraPermissions() }
            testButton.setOnClickListener{ test() }
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
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".png", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            Path = absolutePath
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
        var testImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.footulcer), ImagePreprocessing.INPUT_WIDTH, ImagePreprocessing.INPUT_HEIGHT, true)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException)
        {
            null
        }
        binding.footImage.setImageBitmap(testImage)
        viewModel.prediction(testImage,Path)
    }
    private fun submit(){
        val action = DailySurveyFragmentDirections.actionNavDailySurveyToNavAnalysisRecord()
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