package com.mobile.stashapp.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/graphql")
    fun getData(
        @Body body: RequestBody
    ): Call<ResponseBody>

}