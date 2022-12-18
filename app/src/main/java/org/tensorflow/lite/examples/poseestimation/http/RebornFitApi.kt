package org.tensorflow.lite.examples.poseestimation.http

import org.tensorflow.lite.examples.poseestimation.http.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RebornFitApi {

    @GET("/user")
    fun getUser(@Query("id") id: Long): Call<BaseResVo<User>>

    @GET("/exercise")
    fun getExercise(@Query("id") id: Long): Call<BaseResVo<Exercise>>

    @POST("/compute/exercise")
    fun postComputeExercise(@Body request: ComputeExerciseReqVo): Call<BaseResVo<ComputeExerciseResVo>>
}