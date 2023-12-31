package com.androchef.cameraxfacedetection

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androchef.cameraxfacedetection.camerax.CameraManager
import com.androchef.cameraxfacedetection.camerax.GraphicOverlay
import com.androchef.cameraxfacedetection.listener.ResultListener
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity(), ResultListener {

    private lateinit var cameraManager: CameraManager
    lateinit var previewView_finder: PreviewView
    lateinit var graphicOverlay_finder: GraphicOverlay
    lateinit var btnSwitch: ImageButton
    lateinit var camera_capture_button: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        previewView_finder = findViewById(R.id.previewView_finder)
        camera_capture_button = findViewById(R.id.camera_capture_button)
        graphicOverlay_finder = findViewById(R.id.graphicOverlay_finder)
        btnSwitch = findViewById(R.id.btnSwitch)

        CameraManager.cameraListener = this
        createCameraManager()
        checkForPermission()
        onClicks()
    }

    private fun checkForPermission() {
        if (allPermissionsGranted()) {
            cameraManager.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun onClicks() {

        // Set up the listener for take photo button
        camera_capture_button.setOnClickListener { cameraManager.takePhoto() }
        btnSwitch.setOnClickListener {
            cameraManager.changeCameraSelector()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraManager.startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun createCameraManager() {
        cameraManager = CameraManager(
            this,
            previewView_finder,
            this,
            graphicOverlay_finder,
            getString(R.string.app_name)
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

    override fun cameraCaptureResult(value: String) {
        val intent = Intent()
        intent.putExtra(CameraManager.IMAGE_URI_SAVED, value.toString())
        setResult(RESULT_OK, intent)
        finish()
    }

}