package org.tensorflow.lite.examples.poseestimation.http.model

data class User(
    val userNo: Long,
    val userNm: String,
    val levelNo: Long,
    val levelNm: String,
    val countBtr: Int,
    val angleBtr: Int,
    val circleInc: Int,
    val restInc: Int,
)