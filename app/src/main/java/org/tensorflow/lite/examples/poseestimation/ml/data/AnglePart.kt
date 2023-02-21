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
    LS_N_RS(0, Triple(BodyPart.LEFT_SHOULDER, BodyPart.NOSE, BodyPart.RIGHT_SHOULDER)),
    N_LS_RS(1, Triple(BodyPart.NOSE, BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER)),
    N_RS_LS(2, Triple(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER, BodyPart.LEFT_SHOULDER)),
    N_LS_LE(3, Triple(BodyPart.NOSE, BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW)),
    N_RS_RE(4, Triple(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW)),
    N_LS_LH(5, Triple(BodyPart.NOSE, BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP)),
    N_RS_RH(6, Triple(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP)),
    RS_LS_LE(7, Triple(BodyPart.RIGHT_SHOULDER, BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW)),
    LS_RS_RE(8, Triple(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW)),
    RS_LS_LH(9, Triple(BodyPart.RIGHT_SHOULDER, BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP)),
    LS_RS_RH(10, Triple(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP)),
    LS_LH_RH(11, Triple(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP)),
    RS_RH_LH(12, Triple(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP, BodyPart.LEFT_HIP)),
    LS_LH_LK(13, Triple(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE)),
    RS_RH_RK(14, Triple(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE)),
    LS_LE_LW(15, Triple(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST)),
    RS_RE_RW(16, Triple(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST)),
    LE_LS_LH(17, Triple(BodyPart.LEFT_ELBOW, BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP)),
    RE_RS_RH(18, Triple(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP)),
    RH_LH_LK(19, Triple(BodyPart.RIGHT_HIP, BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE)),
    LH_RH_RK(20, Triple(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE)),
    LH_lK_LA(21, Triple(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE)),
    RH_RK_RA(22, Triple(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE));

    companion object {
        private val map = values().associateBy(AnglePart::position)
        fun fromInt(position: Int): AnglePart = map.getValue(position)
    }
}
