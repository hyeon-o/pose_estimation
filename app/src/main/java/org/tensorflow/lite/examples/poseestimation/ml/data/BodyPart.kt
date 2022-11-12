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

enum class BodyPart(val position: Int, val isShow: Boolean) {
    NOSE(0, true),
    LEFT_EYE(1, false),
    RIGHT_EYE(2, false),
    LEFT_EAR(3, false),
    RIGHT_EAR(4, false),
    LEFT_SHOULDER(5, true),
    RIGHT_SHOULDER(6, true),
    LEFT_ELBOW(7, true),
    RIGHT_ELBOW(8, true),
    LEFT_WRIST(9, true),
    RIGHT_WRIST(10, true),
    LEFT_HIP(11, true),
    RIGHT_HIP(12, true),
    LEFT_KNEE(13, true),
    RIGHT_KNEE(14, true),
    LEFT_ANKLE(15, true),
    RIGHT_ANKLE(16, true);

    companion object {
        private val map = values().associateBy(BodyPart::position)
        fun fromInt(position: Int): BodyPart = map.getValue(position)
    }
}
