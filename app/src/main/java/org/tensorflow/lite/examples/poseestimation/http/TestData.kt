package org.tensorflow.lite.examples.poseestimation.http

import org.tensorflow.lite.examples.poseestimation.exercise.data.UserLevelType
import org.tensorflow.lite.examples.poseestimation.ml.data.AnglePart

interface TestData {

    val user: Map<Long, User>
        get() = mapOf(
            0L to User(userNo = 0L, userLevelType = UserLevelType.A)
        )

    val exercise: Map<Long, Exercise>
        get() = mapOf(
            0L to
                    Exercise(
                        exerciseNo = 0L,
                        set = 3,
                        count = 2,
                        exerciseTime = 10,
                        restTime = 5,
                        rules = listOf(
                            Rule(AnglePart.LEFT_KNEE, 170.0, 90.0, Rule.ExerciseContractionType.Flexion),
                            Rule(AnglePart.RIGHT_KNEE, 170.0, 90.0, Rule.ExerciseContractionType.Flexion)
                        )
                    )

        )
}

data class User(
    val userNo: Long,
    val userLevelType: UserLevelType,
)

data class Exercise(
    val exerciseNo: Long,
    val set: Int,
    val count: Int,
    val exerciseTime: Int,
    val restTime: Int,
    val rules: List<Rule>,
)

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