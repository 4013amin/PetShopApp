package com.example.shop_app_project.data.utils

import com.example.shop_app_project.data.api.API
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object UtilsRetrofit {

    const val BaseUrl = "http://192.168.218.101:2020/"

    val api: API by lazy {
        Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(API::class.java)
    }

}

