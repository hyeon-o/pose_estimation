package org.tensorflow.lite.examples.poseestimation.exercise

import org.tensorflow.lite.examples.poseestimation.exercise.data.AssessType
import org.tensorflow.lite.examples.poseestimation.exercise.data.ExerciseType
import org.tensorflow.lite.examples.poseestimation.exercise.data.UserLevelType
import org.tensorflow.lite.examples.poseestimation.http.ExerciseApi
import org.tensorflow.lite.examples.poseestimation.ml.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.ml.data.Person

class RebornExercise(
    private val userLevelType: UserLevelType,
    private val exerciseType: ExerciseType,
) {

    companion object {
        const val minScore: Float = 0.5f
    }

    private var exerciseApi: ExerciseApi = ExerciseApi()
    private var isExercise: Boolean = false

    var count: Int = 0
    var assess: Map<BodyPart, AssessType> = emptyMap()
    var totalAssess: AssessType = AssessType.None

    fun process(person: Person) {

        // http 통신
        if (person.score >= minScore) {
            val resVo: ClientResVo = exerciseApi.call(
                ClientReqVo(
                    userLevelType = userLevelType,
                    exerciseType = exerciseType,
                    jointAngles = person.jointAngles ?: emptyMap(),
                    isExercise = isExercise,
                )
            )

            // 카운트 증가
            if (!isExercise && resVo.isExercise) {
                count++
            }
            isExercise = resVo.isExercise

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