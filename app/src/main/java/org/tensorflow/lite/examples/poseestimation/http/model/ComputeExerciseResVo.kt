package org.tensorflow.lite.examples.poseestimation.http.model

data class ComputeExerciseResVo (
    val isActivate: Boolean,
    val assess: Map<Int, String> // key: angle_no, value: assess
)