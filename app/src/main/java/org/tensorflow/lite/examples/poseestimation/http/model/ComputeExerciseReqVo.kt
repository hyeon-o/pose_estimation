package org.tensorflow.lite.examples.poseestimation.http.model

data class ComputeExerciseReqVo (
    val isActivate: Boolean,
    val exerciseNo: Long,
    val countBtr: Int,
    val angleBtr: Int,
    val angles: Map<Int, ComputeExerciseAnglesReqVo>,
)

data class ComputeExerciseAnglesReqVo (
    val angleNo: Long,
    val angle: Double,
)