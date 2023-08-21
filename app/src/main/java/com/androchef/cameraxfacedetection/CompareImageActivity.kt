package com.androchef.cameraxfacedetection

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.androchef.cameraxfacedetection.camerax.CameraManager.Companion.IMAGE_URI_SAVED
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark


class CompareImageActivity : AppCompatActivity() {
    lateinit var myBitmap: Bitmap
    private var imagePicked = -1

    lateinit var imageView1: ImageView
    lateinit var imageView2: ImageView
    lateinit var btnMatch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare_image)

        imageView1 = findViewById(R.id.imageView1)
        imageView2 = findViewById(R.id.imageView2)
        btnMatch = findViewById(R.id.btnMatch)

        imageView1.layoutParams.height = 400
        imageView2.layoutParams.height = 400

        imageView1.setOnClickListener {
            imagePicked = PICK_IMAGE_1
            showMenu(imageView1, PICK_IMAGE_1)
        }

        imageView2.setOnClickListener {
            imagePicked = PICK_IMAGE_2
            showMenu(imageView2, PICK_IMAGE_2)
        }

        btnMatch.setOnClickListener {
            detectFaces()
            Log.e("detectImage", "detectImage");

        }
    }

    private fun showMenu(imageView: ImageView?, i: Int) {
        val popupMenu = PopupMenu(this, imageView)
        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.gallery -> {
                    openGallery(i)
                    return@setOnMenuItemClickListener true
                }

                R.id.camera -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivityForResult(intent, CAMERA_IMAGE)
                    // startFaceCaptureActivity(imageView)
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.show()
    }

    private fun detectFaces() {
        // [START set_detector_options]
        val image = InputImage.fromBitmap(myBitmap, 0)

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()
        // [END set_detector_options]

        // [START get_detector]
        val detector = FaceDetection.getClient(options)
        // Or, to use the default option:
        // val detector = FaceDetection.getClient();
        // [END get_detector]

        // [START run_detector]
        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully
                // [START_EXCLUDE]
                // [START get_face_info]

                for (face in faces) {
                    val bounds = face.boundingBox
                    val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                    val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees
                    Log.e("detectImage", bounds.toString());
                    Log.e("detectImage", rotY.toString());
                    Log.e("detectImage", rotZ.toString());

                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                    // nose available):
                    val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)

                    leftEar?.let {
                        val leftEarPos = leftEar.position
                        Log.e("detectImage", "leftEar" + leftEar.toString());

                    }

                    // If classification was enabled:
                    if (face.smilingProbability != null) {
                        val smileProb = face.smilingProbability
                    }
                    if (face.rightEyeOpenProbability != null) {
                        val rightEyeOpenProb = face.rightEyeOpenProbability
                        Log.e("detectImage", "rightEyeOpenProb" + leftEar.toString());

                    }

                    // If face tracking was enabled:
                    if (face.trackingId != null) {
                        val id = face.trackingId
                    }
                }
                // [END get_face_info]
                // [END_EXCLUDE]
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
        // [END run_detector]
    }


    private fun openGallery(id: Int) {
        val pickPhoto =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLuncher.launch(pickPhoto)
    }

    private var imagePickerLuncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // There are no request codes
            val data = result.data
            val imageUri = data?.data!!
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            if (selectedImage != null) {
                val cursor = contentResolver.query(selectedImage, filePathColumn, null, null, null)
                if (cursor != null) {
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath = cursor.getString(columnIndex)
                    myBitmap = BitmapFactory.decodeFile(picturePath)

                    if (imagePicked != -1)
                        if (imagePicked == PICK_IMAGE_1) {
                            imageView1.setImageURI(imageUri)
                        } else if (imagePicked == PICK_IMAGE_2) {
                            imageView2.setImageURI(imageUri)
                        }
                    cursor.close()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_IMAGE) {
                if (data != null && data.hasExtra(IMAGE_URI_SAVED)) {
                    val imageString = data.getStringExtra(IMAGE_URI_SAVED)

                    val imageUri = Uri.parse(imageString)
                    myBitmap = BitmapFactory.decodeFile(imageString)
                    Log.e("detectImage", "myBitmap" + myBitmap.toString());

                    if (imagePicked != -1)
                        if (imagePicked == PICK_IMAGE_1) {
                            imageView1.setImageURI(imageUri)

                        } else if (imagePicked == PICK_IMAGE_2) {
                            imageView2.setImageURI(imageUri)

                        }
                }
            }
        }
    }

    companion object {
        private const val PICK_IMAGE_1 = 1
        private const val PICK_IMAGE_2 = 2
        const val CAMERA_IMAGE = 3
    }
}