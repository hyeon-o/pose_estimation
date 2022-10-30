package org.tensorflow.lite.examples.poseestimation.external.service

import org.tensorflow.lite.examples.poseestimation.data.*
import org.tensorflow.lite.examples.poseestimation.external.entity.Rule

class ExerciseRuleService {

    companion object {
        const val minScore: Float = 0.5f
        val initAssess: Map<BodyPart, AssessType> =
            BodyPart.values().associateWith { AssessType.None }
    }

    private val userLevelType: UserLevelType = UserLevelType.A
    private val exerciseRules: Map<String, List<Rule>> = mapOf(
        ExerciseType.SQUAT.name to
                listOf(
                    Rule(AnglePart.LEFT_KNEE, 170.0, 90.0, Rule.ExerciseContractionType.Flexion),
                    Rule(AnglePart.RIGHT_KNEE, 170.0, 90.0, Rule.ExerciseContractionType.Flexion)
                )
    )

    private var isCount: Boolean = false
    private var count: Int = 0

    fun exercise(exerciseType: String, jointAngles: Map<Int, JointAngle>, score: Float): Pair<Int, Map<BodyPart, AssessType>> {
        val rules = exerciseRules[exerciseType]!!

//        var score = 0f
        val assess = mutableMapOf<BodyPart, AssessType>()

        if (score >= minScore) {

            // count 범위 체크
            val countValue = rules.map {
                if (it.type == Rule.ExerciseContractionType.Extension) {
                    val start = it.end - it.end * (userLevelType.countBtr) / 100
                    jointAngles[it.anglePart.position]!!.angle >= start
                } else {
                    val end = it.end + it.end * (userLevelType.countBtr) / 100
                    jointAngles[it.anglePart.position]!!.angle <= end
                }
            }.all { it }

            // count 범위에 처음 도달했을 때
            if (!isCount && countValue) {
                isCount = true
                count++
            }

            // count 중일 때
            if (isCount) {
                if (!countValue) {
                    // count 범위에서 벗어났을 때
                    isCount = false
                } else {
                    // count 범위에서 벗어나지 않았을 때
                    // angle 범위 체크 및 평가
                    rules.forEach {
                        val start = it.end - it.end * (userLevelType.angleBtr) / 100
                        val end = it.end + it.end * (userLevelType.angleBtr) / 100
                        val currentAssess =
                            if (jointAngles[it.anglePart.position]!!.angle in start..end) AssessType.Good
                            else AssessType.Bad
                        // AnglePart의 2번째 BodyPart에 대한 평가
                        assess[it.anglePart.points.second] =
                            if (assess.containsKey(it.anglePart.points.second)) assess[it.anglePart.points.second]!!.combine(currentAssess)
                            else currentAssess
                    }
                }
            }
        }

        return Pair(count, assess)
    }
}
