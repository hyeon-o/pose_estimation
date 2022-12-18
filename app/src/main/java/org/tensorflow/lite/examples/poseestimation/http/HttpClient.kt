package org.tensorflow.lite.examples.poseestimation.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpClient {

    private const val BASE_URL = "http://172.24.240.1:3000/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val rebornFitApi: RebornFitApi = retrofit.create(RebornFitApi::class.java)
}