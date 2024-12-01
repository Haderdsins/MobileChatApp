package com.example.mobilechatapp.api

import com.example.mobilechatapp.models.LoginResponse
import com.example.mobilechatapp.models.Message
import com.example.mobilechatapp.models.MessageResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Регистрация нового пользователя
 // Ожидаем объект ответа с паролем
    @FormUrlEncoded
    @POST("/addusr")
    suspend fun registerUser(@Field("name") username: String): Response<String>
    // Логин для получения токена
    @GET("/inbox/{username}")
    suspend fun getInbox(@Path("username") username: String, @Header("X-Auth-Token") token: String): Response<List<Message>>
    @POST("/addusr")
    suspend fun registerUser(@Body requestBody: Map<String, String>): Response<String> // Строка ответа

    @POST("/login")
    suspend fun loginUser(@Body requestBody: Map<String, String>): Response<LoginResponse> // ожидаем LoginResponse

    @POST("/messages")
    suspend fun sendMessage(
        @Body message: Message,
        @Header("X-Auth-Token") token: String
    ): Response<MessageResponse>  // Ответом будет MessageResponse

    @GET("/inbox/{username}")
    suspend fun getInbox(@Path("username") username: String, @Query("limit") limit: Int): Response<List<Message>>

    @POST("/messages")
    suspend fun sendMessage(@Body message: Message): Response<Message>

    @POST("/logout")
    suspend fun logoutUser(@Header("X-Auth-Token") token: String): Response<Unit>

    @GET("/channels")
    suspend fun getChannels(): Response<List<String>>

    @GET("/users")
    suspend fun getUsers(): Response<List<String>>

    @GET("/img/{path}")
    suspend fun getImage(@Path("path") path: String): Response<ResponseBody>

    @GET("/thumb/{path}")
    suspend fun getThumbnail(@Path("path") path: String): Response<ResponseBody>
}
