package com.example.mobilechatapp.api

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import com.squareup.moshi.Moshi

object RetrofitClient {
    private val moshi = Moshi.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://faerytea.name:8008/")
        .addConverterFactory(ScalarsConverterFactory.create()) // Для строковых данных
        .addConverterFactory(MoshiConverterFactory.create(moshi)) // Для JSON
        .client(OkHttpClient())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
