package org.tensorflow.lite.examples.poseestimation.http.model

data class Exercise(
    val exerciseNo: Long,
    val type: String, // 운동 타입 (C: 근수축 유형, T: 스트레칭)
    val circleCnt: Int, // 운동 횟수
    val motions: List<Motion>, // 운동의 각도별 움직임
)