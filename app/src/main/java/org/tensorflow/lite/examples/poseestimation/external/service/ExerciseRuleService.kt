package org.tensorflow.lite.examples.poseestimation.external.service

import org.tensorflow.lite.examples.poseestimation.data.AnglePart
import org.tensorflow.lite.examples.poseestimation.data.ExerciseType
import org.tensorflow.lite.examples.poseestimation.data.JointAngle
import org.tensorflow.lite.examples.poseestimation.external.entity.Rule

class ExerciseRuleService {

    companion object {
        const val minScore: Float = 0.5f
    }

    private val toleranceRate: Float = 10f
    private val topRate: Float = 80f
    private val bottomRate: Float = 20f
    private val exerciseRules: Map<String, List<Rule>> = mapOf(
        ExerciseType.SQUAT.name to
                listOf(
                    Rule(AnglePart.LEFT_KNEE, 170.0, 90.0),
                    Rule(AnglePart.RIGHT_KNEE, 170.0, 90.0)
                )
    )

    private var isCount: Boolean = false
    private var count: Int = 0
    private var assess: AssessType = AssessType.Good
    private var accuracyRate: Float = 0f

    fun exercise(exerciseType: String, jointAngles: Map<Int, JointAngle>, score: Float): Pair<Int, AssessType> {
        val rules = exerciseRules[exerciseType]

        if (score >= minScore) {
            if (!isCount) {
                val value = rules?.map {
                    jointAngles[it.anglePart.position]!!.angle < it.end
                }!!.all { it }
                if (value) {
                    count++
                    isCount = true
                }
            } else {
                val value = rules?.map {
                    jointAngles[it.anglePart.position]!!.angle > it.start
                }!!.all { it }
                if (value) {
                    isCount = false
                }
            }
        }

        return Pair(count, assess)
    }

    enum class AssessType(val index: Int) {
        Perfect(1), Good(0), Bad(-1)
    }
}