package com.test.task.mvvm.repository

import com.test.task.mvvm.model.ApiResponseClass
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface Webservice {

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Header(value = "IMSI") imsi: String?,
        @Header(value = "IMEI") imei: String?,
        @Field(value = "username") username: String?,
        @Field(value = "password") password: String?,
    ): Call<ApiResponseClass>
}