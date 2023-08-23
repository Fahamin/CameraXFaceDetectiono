package com.androchef.cameraxfacedetection

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.androchef.cameraxfacedetection.CompareImage.Companion.labels1
import com.androchef.cameraxfacedetection.camerax.CameraManager.Companion.IMAGE_URI_SAVED
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import kotlinx.coroutines.runBlocking


class CompareImageActivity : AppCompatActivity() {
    lateinit var bitmap1: Bitmap
    lateinit var bitmap2: Bitmap

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
            //  val distance = compareFaces(bitmap1, bitmap2)
            val bitmap1 = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.img
            )
            val bitmap2 = BitmapFactory.decodeResource(
                this.resources,
                R.drawable.img
            )

            val result1 = detectFaces(bitmap1);


            val result2 = detectFaces(bitmap2);
            Log.e("fahamin", result2.toString())


            for (r1 in result1) {
                for (r2 in result2) {
                    if (r1.allLandmarks == r2.allLandmarks) {
                        Log.e("fahamin", "similar")
                        break
                    }
                }
            }
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

    private fun detectFaces(bitmap: Bitmap): List<Face> {
        var rface: MutableList<Face> = ArrayList<Face>()

        // [START set_detector_options]
        val image = InputImage.fromBitmap(bitmap, 0)
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
        detector.process(image)
            .addOnSuccessListener { faces ->

                rface = faces

                  for (face in faces) {
                      rface.add(face)
                      /*val bounds = face.boundingBox
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
                      }*/
                  }

            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }

        return rface;
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
                    bitmap1 = BitmapFactory.decodeFile(picturePath)

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

                    if (imagePicked != -1)
                        if (imagePicked == PICK_IMAGE_1) {
                            imageView1.setImageURI(imageUri)
                            bitmap1 = BitmapFactory.decodeFile(imageString)


                        } else if (imagePicked == PICK_IMAGE_2) {
                            imageView2.setImageURI(imageUri)
                            bitmap2 = BitmapFactory.decodeFile(imageString)

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