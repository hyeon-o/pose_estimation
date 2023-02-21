package org.tensorflow.lite.examples.poseestimation.http.model

data class Motion(
    val motionNo: Long,
    val angleNo: Int,
    val start: Int,
    val end: Int,
)