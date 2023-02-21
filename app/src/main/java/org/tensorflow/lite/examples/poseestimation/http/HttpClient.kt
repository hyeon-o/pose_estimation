package org.tensorflow.lite.examples.poseestimation.http

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpClient {

    // ngrok 을 이용해 도메인 임시 생성
    private const val BASE_URL = "https://3b00-183-96-89-2.jp.ngrok.io/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(
            GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        ))
        .build()

    val rebornFitApi: RebornFitApi = retrofit.create(RebornFitApi::class.java)
}