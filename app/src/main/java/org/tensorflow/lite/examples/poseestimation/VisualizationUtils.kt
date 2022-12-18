/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.tensorflow.lite.examples.poseestimation.ml.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.ml.data.Person

object VisualizationUtils {
    /** Radius of circle used to draw keypoints.  */
    private const val CIRCLE_RADIUS = 10f

    /** Width of line used to connected two keypoints.  */
    private const val LINE_WIDTH = 2f

    /** The text size of the person id that will be displayed when the tracker is available.  */
    private const val PERSON_ID_TEXT_SIZE = 30f

    /** Pair of keypoints to draw lines between.  */
    private val bodyJoints = listOf(
        Triple(BodyPart.NOSE, BodyPart.LEFT_SHOULDER, Type.Left),
        Triple(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER, Type.Right),
        Triple(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW, Type.Left),
        Triple(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST, Type.Left),
        Triple(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW, Type.Right),
        Triple(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST, Type.Right),
        Triple(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER, Type.Middle),
        Triple(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP, Type.Left),
        Triple(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP, Type.Right),
        Triple(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP, Type.Middle),
        Triple(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE, Type.Left),
        Triple(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE, Type.Left),
        Triple(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE, Type.Right),
        Triple(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE, Type.Right)
    )

    enum class Type { Left, Middle, Right }

    // Draw line and point indicate body pose
    fun drawBodyKeypoints(
        input: Bitmap,
        person: Person?,
        assess: Map<String, String>
    ): Bitmap {

        val paintLineLeft = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.RED
            style = Paint.Style.STROKE
        }
        val paintLineMiddle = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.WHITE
            style = Paint.Style.STROKE
        }
        val paintLineRight = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.BLUE
            style = Paint.Style.STROKE
        }

        val paintNoneCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.WHITE
            style = Paint.Style.FILL
            // 투명도 설정
            alpha = 120
        }

        val paintBadCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.RED
            style = Paint.Style.FILL
            // 투명도 설정
            alpha = 120
        }

        val paintGoodCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.GREEN
            style = Paint.Style.FILL
            // 투명도 설정
            alpha = 120
        }


        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val originalSizeCanvas = Canvas(output)
        person?.let { p ->

            // joint 간 선
            bodyJoints.forEach {
                val pointA = p.keyPoints[it.first.position]!!.coordinate
                val pointB = p.keyPoints[it.second.position]!!.coordinate
                val paintLine = when(it.third) {
                    Type.Left -> paintLineLeft
                    Type.Middle -> paintLineMiddle
                    Type.Right -> paintLineRight
                }
                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)
            }

            // joint 동그라미
            p.keyPoints.values
                .forEach { point ->
                    if (point.bodyPart.isShow) {
                        originalSizeCanvas.drawCircle(
                            point.coordinate.x,
                            point.coordinate.y,
                            CIRCLE_RADIUS,
                            when(assess[point.bodyPart.position.toString()]) {
                                "BAD" -> paintBadCircle
                                "GOOD" -> paintGoodCircle
                                else -> paintNoneCircle
                            }
                        )
                    }
            }
        }
        return output
    }
}
