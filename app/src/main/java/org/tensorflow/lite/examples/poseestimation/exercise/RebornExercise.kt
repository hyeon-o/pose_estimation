package org.tensorflow.lite.examples.poseestimation.exercise

import android.os.CountDownTimer
import org.tensorflow.lite.examples.poseestimation.exercise.data.AssessType
import org.tensorflow.lite.examples.poseestimation.http.Exercise
import org.tensorflow.lite.examples.poseestimation.http.ExerciseApi
import org.tensorflow.lite.examples.poseestimation.http.User
import org.tensorflow.lite.examples.poseestimation.ml.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.ml.data.Person

class RebornExercise(
    private val user: User,
    private val exercise: Exercise,
    private val listener: RebornExerciseListener? = null
) {

    companion object {
        const val minScore: Float = 0.5f
        var timer: CountDownTimer? = null
    }

    // 운동 동작 중
    private var isActivate: Boolean = false
    // 쉬는 중
    private var isRest: Boolean = false

    var circleCount: Int = 0
    var repCount: Int = 0
    var assess: Map<BodyPart, AssessType> = emptyMap()
    var totalAssess: AssessType = AssessType.None

    init {
        listener?.onExercise()
    }

    fun process(person: Person) {

        if (isRest) {
            return
        }

        // http 통신
        if (person.score >= minScore) {
            val resVo: ClientResVo = ExerciseApi.exercise(
                ClientReqVo(
                    userLevelType = user.userLevelType,
                    exerciseNo = exercise.exerciseNo,
                    jointAngles = person.jointAngles ?: emptyMap(),
                    isActivate = isActivate,
                )
            )

            // 운동 카운트
            if (!isActivate && resVo.isActivate) {
                // deactivate -> activate
                // rep 카운트 증가
                repCount++
            } else if (repCount == exercise.rep && isActivate && !resVo.isActivate) {
                // 목표 rep 카운트 달성
                // activate -> deactivate
                finishCircle()
            }
            isActivate = resVo.isActivate

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

    fun startCircle() {
        isRest = false
        listener?.onExercise()
    }

    fun finishCircle() {
        if (!isRest) {
            circleCount++
        }
        if (circleCount == exercise.circle) {
            listener?.onFinish()
        } else {
            isRest = true
            repCount = 0
            listener?.onRest()
        }
    }

    interface RebornExerciseListener {
        fun onExercise()
        fun onRest()
        fun onFinish()
    }
}