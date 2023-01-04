package org.tensorflow.lite.examples.poseestimation.http.model

data class Exercise(
    val exerciseNo: Long,
    val circleCnt: Int,
    val repCnt: Int,
    val circleTime: Int, // 초단위
    val repTime: Int, // 초단위
    val type: String, // 운동 타입 (C: 횟수 중요, T: 시간 중요)
)