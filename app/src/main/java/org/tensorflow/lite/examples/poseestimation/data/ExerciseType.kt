package org.tensorflow.lite.examples.poseestimation.data

enum class ExerciseType(val code: String) {
    SQUAT("H00");

    companion object{
        private val map = ExerciseType.values().associateBy(ExerciseType::code)
        fun fromString(code: String): ExerciseType = map.getValue(code)
    }
}