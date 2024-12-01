package com.example.mobilechatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.mobilechatapp.api.RetrofitClient
import com.example.mobilechatapp.models.LoginResponse
import androidx.compose.ui.platform.LocalContext
import com.example.mobilechatapp.models.Message
import com.example.mobilechatapp.models.MessageData
import com.example.mobilechatapp.models.MessageResponse
import com.example.mobilechatapp.models.MessageText

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen() // Показываем экран регистрации
        }
    }

    internal fun registerUser(username: String, callback: (String?, String?) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.registerUser(username)

                if (response.isSuccessful) {
                    val password = response.body() // Пример ответа: "password: '<password>'"
                    callback(password, null)
                } else {
                    callback(null, "Registration failed: ${response.code()}")
                }
            } catch (e: Exception) {
                callback(null, "Error: ${e.message}")
            }
        }
    }
    internal fun sendMessage(message: Message, token: String, callback: (MessageResponse?, String?) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.sendMessage(message, token)

                if (response.isSuccessful) {
                    val messageResponse = response.body() // Ответ с id сообщения
                    callback(messageResponse, null)
                } else {
                    callback(null, "Failed to send message: ${response.code()}")
                }
            } catch (e: Exception) {
                callback(null, "Error: ${e.message}")
            }
        }
    }
    internal fun loginUserInCoroutine(username: String, password: String, callback: (LoginResponse?, String?) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.loginUser(mapOf("name" to username, "pwd" to password))

                if (response.isSuccessful) {
                    val loginResponse = response.body() // Получаем тело ответа, например, LoginResponse с токеном
                    if (loginResponse != null) {
                        callback(loginResponse, null) // Отправляем ответ
                    } else {
                        callback(null, "Login failed: No body in response")
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    callback(null, "Login failed: ${response.code()}, $errorResponse")
                }
            } catch (e: Exception) {
                callback(null, "Error: ${e.message}")
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen() { // Экран регистрации и отправки сообщений
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var registrationMessage by remember { mutableStateOf<String?>(null) }
    var messageText by remember { mutableStateOf("") }  // Для сообщения
    var messageResponse by remember { mutableStateOf<String?>(null) }  // Ответ от API по отправке сообщения

    // Получаем контекст
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Register Screen", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                registrationMessage = null

                // Регистрация пользователя
                (context as? MainActivity)?.registerUser(username) { result, error ->
                    isLoading = false
                    if (result != null) {
                        registrationMessage = result
                    } else {
                        errorMessage = error
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text("Register")
        }

        registrationMessage?.let {
            Text("Success: $it", style = MaterialTheme.typography.bodyLarge)
        }

        errorMessage?.let {
            Text("Error: $it", style = MaterialTheme.typography.bodyLarge)
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password.orEmpty(),
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!username.isBlank() && !password.isNullOrBlank()) {
                    (context as? MainActivity)?.loginUserInCoroutine(username, password ?: "") { loginResponse, error ->
                        if (loginResponse != null) {
                            // Токен получен, можно переходить дальше
                            println("Token: ${loginResponse.token}")
                            // Теперь можно отправлять сообщения
                            val message = Message(
                                to = "1@channel",  // Например, канал 1@channel
                                data = MessageData(MessageText("Hello from $username"))
                            )

                            (context as? MainActivity)?.sendMessage(message, loginResponse.token) { response, sendError ->
                                if (response != null) {
                                    messageResponse = "Message sent successfully with id: ${response.id}"
                                } else {
                                    messageResponse = "Failed to send message: $sendError"
                                }
                            }
                        } else {
                            errorMessage = error
                        }
                    }
                }
            }
        ) {
            Text("Login")
        }

        // Сообщение о статусе отправки
        messageResponse?.let {
            Text(it, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    RegisterScreen() // Предпросмотр
}
