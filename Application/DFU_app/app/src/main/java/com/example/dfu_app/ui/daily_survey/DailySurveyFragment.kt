package com.example.dfu_app.ui.daily_survey

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dfu_app.databinding.FragmentDailySurveyBinding
import com.example.dfu_app.imageprocessor.ImagePreprocessing
import com.example.dfu_app.model.PytorchPrediction
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


class DailySurveyFragment: Fragment() {
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
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    binding.footImage.setImageBitmap(ImagePreprocessing.loadingImg( Path ))
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
    private fun bind( ) {
        binding.apply {
            submitDailSurveyButton.setOnClickListener { submit() }
            cameraButton.setOnClickListener{askCameraPermissions()
            retrievedButton.setOnClickListener{ retrieve()}
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
        { null }
        photoFile?.also {  photoURI =
                            FileProvider.getUriForFile(requireContext(), "com.example.android.fileprovider", it) }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)
        resultLauncher.launch(intent)
        }
    private fun submit(){
        //binding.footImage.setImageDrawable(null)
        val action = DailySurveyFragmentDirections.actionNavDailySurveyToNavAnalysisRecord()
        this.findNavController().navigate(action)
    }
    private fun retrieve(){
        val bitmap = ImagePreprocessing.loadingImg( Path )
        try{
            val pytorchModel = PytorchPrediction(requireContext().assets)
            val predictBitmap = pytorchModel.modelPredict(bitmap)
            binding.footImage.setImageBitmap(predictBitmap)
            predictBitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(Path))
        }
        catch( e: IOException ){

        }

        //val pytorchModel = PytorchPrediction(requireContext().assets)
        //val predictBitmap = pytorchModel.modelPredict(bitmap)
        //binding.footImage.setImageBitmap(predictBitmap)
//        val rect = Rect(0,0,400,400)
//        val rest = com.example.dfu_app.imageprocessor.Result(0,0.0f,rect)
//        val testRest = listOf(rest)
//        val predictBitmap = DrawBoundingBox(bitmap,testRest)
//        binding.footImage.setImageBitmap(predictBitmap)
//        predictBitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(path));
    }
    /*
    fun assetFilePath(context: Context, asset: String): String {
        val file = File(context.filesDir, asset)

        try {
            val inpStream: InputStream = context.assets.open(asset)
            try {
                val outStream = FileOutputStream(file, false)
                val buffer = ByteArray(4 * 1024)
                var read: Int

                while (true) {
                    read = inpStream.read(buffer)
                    if (read == -1) {
                        break
                    }
                    outStream.write(buffer, 0, read)
                }
                outStream.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
    */
}