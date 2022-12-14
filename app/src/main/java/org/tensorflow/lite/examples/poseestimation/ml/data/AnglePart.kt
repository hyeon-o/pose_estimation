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

package org.tensorflow.lite.examples.poseestimation.ml.data

/**
 * 리본에서 작성한 각 관절각도에 대한 정의
 * 3개의 BodyPart 이용
 */
enum class AnglePart(val position: Int, val points: Triple<BodyPart, BodyPart, BodyPart>) {
    LEFT_KNEE(1, Triple(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE)),
    RIGHT_KNEE(2, Triple(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)),
    LOWER_LEFT_HIP(3, Triple(BodyPart.RIGHT_HIP, BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE)),
    LOWER_RIGHT_HIP(4, Triple(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE));

    companion object {
        private val map = values().associateBy(AnglePart::position)
        fun fromInt(position: Int): AnglePart = map.getValue(position)
    }
}
