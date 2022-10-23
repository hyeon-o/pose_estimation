package org.tensorflow.lite.examples.poseestimation.external.entity

import org.tensorflow.lite.examples.poseestimation.data.AnglePart

data class Rule(
    val anglePart: AnglePart,
    val start: Double,
    val end: Double
)
