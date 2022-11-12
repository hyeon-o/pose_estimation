package org.tensorflow.lite.examples.poseestimation.http

import org.tensorflow.lite.examples.poseestimation.exercise.ClientReqVo
import org.tensorflow.lite.examples.poseestimation.exercise.ClientResVo
import org.tensorflow.lite.examples.poseestimation.exercise.data.AssessType
import org.tensorflow.lite.examples.poseestimation.ml.data.BodyPart

class ExerciseApi: TestData {

    fun call(reqVo: ClientReqVo): ClientResVo {

        val rules = exercise[reqVo.exerciseType]!!

        var isExercise = false
        val assess = mutableMapOf<BodyPart, AssessType>()


        // 운동 동작 범위 체크
        val countValue = rules.map {
            if (it.type == Rule.ExerciseContractionType.Extension) {
                val start = it.end - it.end * (reqVo.userLevelType.countBtr) / 100
                reqVo.jointAngles[it.anglePart.position]!!.angle >= start
            } else {
                val end = it.end + it.end * (reqVo.userLevelType.countBtr) / 100
                reqVo.jointAngles[it.anglePart.position]!!.angle <= end
            }
        }.all { it }

        // 운동 동작 준비 중 count 범위에 처음 도달했을 때
        if (!reqVo.isExercise && countValue) {
            isExercise = true
        }

        // 운동 동작 중일 때
        if (reqVo.isExercise) {
            if (!countValue) {
                // 운동 동작 범위에서 벗어났을 때
                isExercise = false
            } else {
                // 운동 동작 범위에서 벗어나지 않았을 때
                isExercise = true
                // angle 범위 체크 및 평가
                rules.forEach {
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

        return ClientResVo(isExercise = isExercise, assess = assess)
    }
}
