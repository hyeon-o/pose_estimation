package org.tensorflow.lite.examples.poseestimation.http.model

data class BaseResVo<T> (
    val message: String,
    val data: T
)