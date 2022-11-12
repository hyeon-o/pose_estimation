package org.tensorflow.lite.examples.poseestimation.http

import org.tensorflow.lite.examples.poseestimation.exercise.data.ExerciseType
import org.tensorflow.lite.examples.poseestimation.ml.data.AnglePart

interface TestData {

    val exercise: Map<ExerciseType, List<Rule>>
        get() = mapOf(
            ExerciseType.SQUAT to
                    listOf(
                        Rule(AnglePart.LEFT_KNEE, 170.0, 90.0, Rule.ExerciseContractionType.Flexion),
                        Rule(AnglePart.RIGHT_KNEE, 170.0, 90.0, Rule.ExerciseContractionType.Flexion)
                    )
        )
}

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