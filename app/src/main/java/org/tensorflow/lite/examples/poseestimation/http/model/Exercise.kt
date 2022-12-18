package org.tensorflow.lite.examples.poseestimation.http.model

data class Exercise(
    val exerciseNo: Long,
    val circleCnt: Int,
    val repCnt: Int,
    val circleTime: Int, // 초단위
    val restTime: Int, // 초단위
)