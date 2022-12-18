package org.tensorflow.lite.examples.poseestimation.exercise

import android.os.CountDownTimer
import android.util.Log
import org.tensorflow.lite.examples.poseestimation.http.HttpClient
import org.tensorflow.lite.examples.poseestimation.http.model.*
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
        var timer: CountDownTimer? = null
    }

    // 운동 동작 중
    private var isActivate: Boolean = false
    // 쉬는 중
    private var isRest: Boolean = false

    var circleCount: Int = 0
    var repCount: Int = 0
    var assess: Map<String, String> = emptyMap()
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
                    angleBtr = user.angleBtr,
                    angles = person.jointAngles?.mapValues { it.value.toMap() } ?: emptyMap(),
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
                    } else if (repCount == exercise.repCnt && isActivate && !resVo.isActivate) {
                        // 목표 rep 카운트 달성
                        // activate -> deactivate
                        finishCircle()
                    }
                    isActivate = resVo.isActivate

                    // BodyPart별 평가
                    assess = resVo.assess

                    // 종합 평가
                    if (resVo.assess.isNotEmpty()) {
                        resVo.assess.values.forEach {
                            if (totalAssess.isNotBlank()) {
                                totalAssess = it
                            } else if (totalAssess == "GOOD" && it == "GOOD") {
                                totalAssess = "GOOD"
                            } else {
                                totalAssess = "BAD"
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResVo<ComputeExerciseResVo>>, t: Throwable) {
                    Log.e(null, null, t)
                }
            })
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