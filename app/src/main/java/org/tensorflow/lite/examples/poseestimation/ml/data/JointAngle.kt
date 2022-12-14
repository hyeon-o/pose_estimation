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

import org.tensorflow.lite.examples.poseestimation.http.model.ComputeExerciseAnglesReqVo

data class JointAngle(val anglePart: AnglePart, val angle: Double) {
    fun toReq() = ComputeExerciseAnglesReqVo(
        angleNo = anglePart.position.toLong(),
        angle = angle
    )
}
