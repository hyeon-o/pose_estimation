package org.tensorflow.lite.examples.poseestimation.http

import org.tensorflow.lite.examples.poseestimation.exercise.ClientReqVo
import org.tensorflow.lite.examples.poseestimation.exercise.ClientResVo
import org.tensorflow.lite.examples.poseestimation.exercise.data.AssessType
import org.tensorflow.lite.examples.poseestimation.ml.data.BodyPart

object ExerciseApi: TestData {

    /**
     * 사용자 조회
     */
    fun getUser(userNo: Long): User {
        return user[userNo]!!
    }

    /**
     * 운동 조회
     */
    fun getExercise(exerciseNo: Long): Exercise {
        return exercise[exerciseNo]!!
    }

    /**
     * 운동 로직
     */
    fun exercise(reqVo: ClientReqVo): ClientResVo {

        val motions = exercise[reqVo.exerciseNo]!!.motions

        var isActivate = false
        val assess = mutableMapOf<BodyPart, AssessType>()

        // 운동 동작 범위 체크
        val countValue = motions.map {
            if (it.type == Motion.ExerciseContractionType.Extension) {
                val start = it.end - it.end * (reqVo.userLevelType.countBtr) / 100
                reqVo.jointAngles[it.anglePart.position]!!.angle >= start
            } else {
                val end = it.end + it.end * (reqVo.userLevelType.countBtr) / 100
                reqVo.jointAngles[it.anglePart.position]!!.angle <= end
            }
        }.all { it }

        // 운동 동작 준비 중 count 범위에 처음 도달했을 때
        if (!reqVo.isActivate && countValue) {
            isActivate = true
        }

        // 운동 동작 중일 때
        if (reqVo.isActivate) {
            if (!countValue) {
                // 운동 동작 범위에서 벗어났을 때
                isActivate = false
            } else {
                // 운동 동작 범위에서 벗어나지 않았을 때
                isActivate = true
                // angle 범위 체크 및 평가
                motions.forEach {
                    val start = it.end - it.end * (reqVo.userLevelType.angleBtr) / 100
                    val end = it.end + it.end * (reqVo.userLevelType.angleBtr) / 100
                    val currentAssess =
                        if (reqVo.jointAngles[it.anglePart.position]!!.angle in start..end) AssessType.Good
                        else AssessType.Bad
                    // AnglePart의 2번째 BodyPart에 대한 평가
                    assess[it.anglePart.points.second] =
                        if (assess.containsKey(it.anglePart.points.second)) assess[it.anglePart.points.second]!!.combine(currentAssess)
                        else currentAssess
                }
            }
        }

        return ClientResVo(isActivate = isActivate, assess = assess)
    }
}

