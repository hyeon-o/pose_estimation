package org.tensorflow.lite.examples.poseestimation.exercise

import android.util.Log
import org.tensorflow.lite.examples.poseestimation.http.HttpClient
import org.tensorflow.lite.examples.poseestimation.http.model.*
import org.tensorflow.lite.examples.poseestimation.ml.data.AnglePart
import org.tensorflow.lite.examples.poseestimation.ml.data.Person
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RebornExercise(
    private val user: User,
    private val exercise: Exercise,
    private val listener: RebornExerciseListener? = null
) {

    companion object {
        const val minScore: Float = 0.5f
    }

    // 운동 동작 중
    private var isActivate: Boolean = false
    // 쉬는 중
    private var isRest: Boolean = false

    var circleCount: Int = 0
    var repCount: Int = 0
    var assess: Map<AnglePart, String> = emptyMap()
    var totalAssess: String = ""

    init {
        listener?.onExercise()
    }

    fun process(person: Person) {

        if (isRest) {
            return
        }

        // http 통신
        if (person.score >= minScore) {
            val call = HttpClient.rebornFitApi.postComputeExercise(
                ComputeExerciseReqVo(
                    isActivate = isActivate,
                    exerciseNo = exercise.exerciseNo,
                    countBtr = user.countBtr,
                    assessBtr = user.angleBtr,
                    angles = person.jointAngles?.map { it.value.toReq() } ?: emptyList(),
                    motions = exercise.motions,
                )
            )
            call.enqueue(object : Callback<BaseResVo<ComputeExerciseResVo>> {
                override fun onResponse(
                    call: Call<BaseResVo<ComputeExerciseResVo>>,
                    response: Response<BaseResVo<ComputeExerciseResVo>>
                ) {
                    val resVo = response.body()!!.data

                    // 운동 카운트
                    if (!isActivate && resVo.isActivate) {
                        // deactivate -> activate
                        // rep 카운트 증가
                        repCount++
                    } else if (exercise.type == "C" && repCount == user.repCnt && isActivate && !resVo.isActivate) {
                        // 목표 rep 카운트 달성
                        // activate -> deactivate
                        finishCircle()
                    }
                    isActivate = resVo.isActivate

                    // ANGLE 별 평가
                    assess = resVo.assess.mapKeys { AnglePart.fromInt(it.key) }

//                    // 종합 평가
//                    if (resVo.assess.isNotEmpty()) {
//                        resVo.assess.values.forEach {
//                            if (totalAssess.isNotBlank()) {
//                                totalAssess = it
//                            } else if (totalAssess == "GOOD" && it == "GOOD") {
//                                totalAssess = "GOOD"
//                            } else {
//                                totalAssess = "BAD"
//                            }
//                        }
//                    }
                }

                override fun onFailure(call: Call<BaseResVo<ComputeExerciseResVo>>, t: Throwable) {
                    Log.e(t.message, t.message, t)
                }
            })
        }
    }

    fun startCircle() {
        isRest = false
        listener?.onExercise()
    }

    fun finishCircle() {
        circleCount++

        if (circleCount == exercise.circleCnt) {
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