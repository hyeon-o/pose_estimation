package org.tensorflow.lite.examples.poseestimation.exercise.data

enum class AssessType {
    None, Bad, Good;

    fun combine(other: AssessType): AssessType {
        return if (this <= other) this else other
    }
}