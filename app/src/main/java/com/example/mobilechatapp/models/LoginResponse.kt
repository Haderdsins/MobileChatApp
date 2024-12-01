package com.example.mobilechatapp.models

data class RegisterResponse(
    val message: String,  // Если сервер возвращает сообщение
    val success: Boolean  // Например, флаг успешности
)

data class LoginResponse(
    val token: String
)
data class LoginData(
    val name: String,
    val pwd: String
)