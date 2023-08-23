package com.androchef.cameraxfacedetection

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel
import kotlinx.coroutines.runBlocking

class CompareImage(var image1: FirebaseVisionImage, var image2: FirebaseVisionImage) {
    init {
        compareImage()
    }

    companion object {
        lateinit var labels1: List<FirebaseVisionImageLabel>
        lateinit var labels2: List<FirebaseVisionImageLabel>

    }

    fun compareImage() {
        val labeler = FirebaseVision.getInstance().onDeviceImageLabeler


        runBlocking {
            var task1 = labeler.processImage(image1)
            task1.addOnCompleteListener {
                labels1 = task1.result!!
            }
        }

        runBlocking {
            var task = labeler.processImage(image2)
            task.addOnCompleteListener {
                labels2 = task.result!!
            }
        }


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
        if (areSimilar) {
            Log.d("TAG", "The two images are similar.")
        } else {
            Log.d("TAG", "The two images are not similar.")
        }
    }
}