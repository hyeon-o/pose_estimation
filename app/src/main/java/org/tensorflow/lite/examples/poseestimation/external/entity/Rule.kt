package org.tensorflow.lite.examples.poseestimation.external.entity

import org.tensorflow.lite.examples.poseestimation.data.AnglePart

data class Rule(
    val anglePart: AnglePart,
    val start: Double,
    val end: Double,
    val type: ExerciseContractionType,
) {

    enum class ExerciseContractionType {
        // 각도 커짐
        Extension,
        // 각도 작아짐
        Flexion;
    }
}
