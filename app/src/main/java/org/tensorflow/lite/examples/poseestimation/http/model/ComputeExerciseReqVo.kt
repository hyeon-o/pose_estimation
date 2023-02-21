package org.tensorflow.lite.examples.poseestimation.http.model

data class ComputeExerciseReqVo (
    val isActivate: Boolean,
    val exerciseNo: Long,
    val countBtr: Int,
    val assessBtr: Int,
    val angles: List<ComputeExerciseAnglesReqVo>,
    val motions: List<Motion>,
)

data class ComputeExerciseAnglesReqVo (
    val angleNo: Long,
    val angle: Double,
)