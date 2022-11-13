package org.tensorflow.lite.examples.poseestimation.exercise

import org.tensorflow.lite.examples.poseestimation.exercise.data.AssessType
import org.tensorflow.lite.examples.poseestimation.http.ExerciseApi
import org.tensorflow.lite.examples.poseestimation.http.User
import org.tensorflow.lite.examples.poseestimation.ml.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.ml.data.Person

class RebornExercise(
    private val user: User,
    private val exerciseNo: Long,
) {

    companion object {
        const val minScore: Float = 0.5f
    }

    private var isExercise: Boolean = false

    var count: Int = 0
    var assess: Map<BodyPart, AssessType> = emptyMap()
    var totalAssess: AssessType = AssessType.None

    fun process(person: Person) {

        // http 통신
        if (person.score >= minScore) {
            val resVo: ClientResVo = ExerciseApi.exercise(
                ClientReqVo(
                    userLevelType = user.userLevelType,
                    exerciseNo = exerciseNo,
                    jointAngles = person.jointAngles ?: emptyMap(),
                    isExercise = isExercise,
                )
            )

            // 운동 카운트
            if (!isExercise && resVo.isExercise) {
                count++
            }
            isExercise = resVo.isExercise

            // 세트 카운트

            // BodyPart별 평가
            assess = resVo.assess

            // 종합 평가
            if (resVo.assess.isNotEmpty()) {
                totalAssess = resVo.assess.values.reduce { total, value ->
                    total.combine(value)
                }
            }
        }
    }
}