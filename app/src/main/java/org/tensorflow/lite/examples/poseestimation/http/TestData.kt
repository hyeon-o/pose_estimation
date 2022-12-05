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
                        circle = 2,
                        rep = 2,
                        circleTime = 10,
                        restTime = 3,
                        motions = listOf(
                            Motion(AnglePart.LEFT_KNEE, 170.0, 90.0, Motion.ExerciseContractionType.Flexion),
                            Motion(AnglePart.RIGHT_KNEE, 170.0, 90.0, Motion.ExerciseContractionType.Flexion)
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
    val circle: Int,
    val rep: Int,
    val circleTime: Int, // 초단위
    val restTime: Int, // 초단위
    val motions: List<Motion>,
)

data class Motion(
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