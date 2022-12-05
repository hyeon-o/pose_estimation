package org.tensorflow.lite.examples.poseestimation.exercise

import org.tensorflow.lite.examples.poseestimation.ml.data.JointAngle
import org.tensorflow.lite.examples.poseestimation.exercise.data.UserLevelType

data class ClientReqVo (
    val userLevelType: UserLevelType,
    val exerciseNo: Long,
    val jointAngles: Map<Int, JointAngle>,
    val isActivate: Boolean,
)