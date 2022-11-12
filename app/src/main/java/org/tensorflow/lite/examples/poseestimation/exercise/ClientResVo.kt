package org.tensorflow.lite.examples.poseestimation.exercise

import org.tensorflow.lite.examples.poseestimation.exercise.data.AssessType
import org.tensorflow.lite.examples.poseestimation.ml.data.BodyPart

data class ClientResVo (
    val isExercise: Boolean,
    val assess: Map<BodyPart, AssessType>
)