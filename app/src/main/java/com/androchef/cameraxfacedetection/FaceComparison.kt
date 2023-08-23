package com.androchef.cameraxfacedetection

import android.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import kotlin.math.pow
import kotlin.math.sqrt


object FaceComparison {
    fun compareFaces(
        image1: Bitmap,
        image2: Bitmap
    ): Boolean {

        val iv1: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(image1)
        val labeler: FirebaseVisionImageLabeler =
            FirebaseVision.getInstance().getOnDeviceImageLabeler()
        val labels1: Task<MutableList<FirebaseVisionImageLabel>> =
            labeler.processImage(FirebaseVisionImage.fromBitmap(image1))
        val labels2: Task<MutableList<FirebaseVisionImageLabel>> =
            labeler.processImage(FirebaseVisionImage.fromBitmap(image1))

        var areSimilar = false
        if (labels1.result == labels2.result) {
            areSimilar = true
        }

// Display the results.
// Display the results.
        if (areSimilar) {
            return true
            Log.d("TAG", "The two images are similar.")
        } else {
            return false

            Log.d("TAG", "The two images are not similar.")
        }


    }

   /* fun CompareTwoImage(  image1: Bitmap,
                          image2: Bitmap) {
        val labeler = FirebaseVision.getInstance().onDeviceImageLabeler

// Use the Image Labeler to label each image.
// Use the Image Labeler to label each image.
        val labels1: Task<MutableList<FirebaseVisionImageLabel>> = labeler.processImage(image1)
        val labels2: Task<MutableList<FirebaseVisionImageLabel>> = labeler.processImage(image2)
// Compare the labels of the two images to see if they are similar.
// Compare the labels of the two images to see if they are similar.
        var areSimilar = false
        for (label1 in labels1) {
            for (label2 in labels2) {
                if (label1.text == label2.text) {
                    areSimilar = true
                    break
                }
            }
        }
// Display the result.
// Display the result.
        if (areSimilar) {
            Log.d("TAG", "The two images are similar.")
        } else {
            Log.d("TAG", "The two images are not similar.")
        }

    }
*/
    private fun getFaceFeatures(face: Face): FloatArray {

        Log.e("fahamin", face.allLandmarks.toString())
        // Get the face landmarks.
        val landmarks: List<FaceLandmark> = face.allLandmarks
        // Calculate the face features.
        val features = FloatArray(128)
        for (landmark in landmarks) {
            features[landmark.position.x.toInt()]++
            features[landmark.position.y.toInt()]++
        }
        return features
    }

    private fun calculateDistance(features1: FloatArray, features2: FloatArray): Float {
        // Calculate the Euclidean distance between the two feature vectors.
        var distance = 0f
        for (i in features1.indices) {
            distance += (features1[i] - features2[i]).toDouble().pow(2.0).toFloat()
        }
        return sqrt(distance.toDouble()).toFloat()
    }
}