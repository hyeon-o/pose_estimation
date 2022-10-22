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
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.data.PartType
import org.tensorflow.lite.examples.poseestimation.data.Person
import kotlin.math.max

object VisualizationUtils {
    /** Radius of circle used to draw keypoints.  */
    private const val CIRCLE_RADIUS = 10f

    /** Width of line used to connected two keypoints.  */
    private const val LINE_WIDTH = 2f

    /** The text size of the person id that will be displayed when the tracker is available.  */
    private const val PERSON_ID_TEXT_SIZE = 30f

    /** Distance from person id to the nose keypoint.  */
    private const val PERSON_ID_MARGIN = 6f

    /** Pair of keypoints to draw lines between.  */
    private val bodyJoints = listOf(
//        Pair(BodyPart.NOSE, BodyPart.LEFT_EYE),
//        Pair(BodyPart.NOSE, BodyPart.RIGHT_EYE),
//        Pair(BodyPart.LEFT_EYE, BodyPart.LEFT_EAR),
//        Pair(BodyPart.RIGHT_EYE, BodyPart.RIGHT_EAR),
        Triple(BodyPart.NOSE, BodyPart.LEFT_SHOULDER, PartType.Left),
        Triple(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER, PartType.Right),
        Triple(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW, PartType.Left),
        Triple(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST, PartType.Left),
        Triple(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW, PartType.Right),
        Triple(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST, PartType.Right),
        Triple(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER, PartType.Middle),
        Triple(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP, PartType.Left),
        Triple(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP, PartType.Right),
        Triple(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP, PartType.Middle),
        Triple(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE, PartType.Left),
        Triple(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE, PartType.Left),
        Triple(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE, PartType.Right),
        Triple(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE, PartType.Right)
    )

    // Draw line and point indicate body pose
    fun drawBodyKeypoints(
        input: Bitmap,
        persons: List<Person>,
        isTrackerEnabled: Boolean = false
    ): Bitmap {
        val paintCircle = Paint().apply {
            strokeWidth = CIRCLE_RADIUS
            color = Color.WHITE
            style = Paint.Style.FILL
            // jhyeon: 투명도 설정
            alpha = 120
        }

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

        val paintText = Paint().apply {
            textSize = PERSON_ID_TEXT_SIZE
            color = Color.BLUE
            textAlign = Paint.Align.LEFT
        }

        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val originalSizeCanvas = Canvas(output)
        persons.forEach { person ->
            // draw person id if tracker is enable
            if (isTrackerEnabled) {
                person.boundingBox?.let {
                    val personIdX = max(0f, it.left)
                    val personIdY = max(0f, it.top)

                    originalSizeCanvas.drawText(
                        person.id.toString(),
                        personIdX,
                        personIdY - PERSON_ID_MARGIN,
                        paintText
                    )
                    originalSizeCanvas.drawRect(it, paintLineMiddle)
                }
            }

            // joint 간 선
            bodyJoints.forEach {
                val pointA = person.keyPoints[it.first.position].coordinate
                val pointB = person.keyPoints[it.second.position].coordinate
                val paintLine = when(it.third) {
                    PartType.Left -> paintLineLeft
                    PartType.Middle -> paintLineMiddle
                    PartType.Right -> paintLineRight
                }
                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)
            }

            // joint 동그라미
            person.keyPoints
                .filter {
                    it.bodyPart !in arrayOf(BodyPart.LEFT_EYE, BodyPart.RIGHT_EYE, BodyPart.LEFT_EAR, BodyPart.RIGHT_EAR)
                }
                .forEach { point ->
                    originalSizeCanvas.drawCircle(
                        point.coordinate.x,
                        point.coordinate.y,
                        CIRCLE_RADIUS,
                        paintCircle
                )
            }
        }
        return output
    }
}
