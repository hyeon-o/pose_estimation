package org.tensorflow.lite.examples.poseestimation.exercise

import org.tensorflow.lite.examples.poseestimation.exercise.data.ExerciseType
import org.tensorflow.lite.examples.poseestimation.ml.data.JointAngle
import org.tensorflow.lite.examples.poseestimation.exercise.data.UserLevelType

data class ClientReqVo (
    val userLevelType: UserLevelType,
    val exerciseType: ExerciseType,
    val jointAngles: Map<Int, JointAngle>,
    val isExercise: Boolean,
)