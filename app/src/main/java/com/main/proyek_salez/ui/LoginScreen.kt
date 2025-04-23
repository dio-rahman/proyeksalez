package com.main.proyek_salez.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.main.proyek_salez.R
import com.main.proyek_salez.data.entities.User
import com.main.proyek_salez.data.viewmodel.AuthViewModel
import androidx.activity.compose.BackHandler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.main.proyek_salez.utils.Event
import com.main.proyek_salez.ui.theme.*


private val _loginResult = MutableLiveData<Event<Result<User>>>()
val loginResult: LiveData<Event<Result<User>>> = _loginResult


@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: (User) -> Unit
) {

    BackHandler(enabled = true) {
        // Prevent going back to the dashboard
    }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Observe login result
    val loginResult by viewModel.loginResult.observeAsState()

    LaunchedEffect(loginResult) {
        loginResult?.getContentIfNotHandled()?.let { result ->
            isLoading = false
            result.fold(
                onSuccess = { user ->
                    // Login berhasil, navigasi berdasarkan role
                    onLoginSuccess(user)
                },
                onFailure = { e ->
                    // Login gagal, tampilkan pesan error
                    errorMessage = e.message ?: "Login gagal"
                }
            )
        }
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Putih,
            Jingga,
            UnguTua
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground)
    ) {
        IconButton(
            onClick = { /* TODO: Handle back navigation */ },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = UnguTua
            )
        }

        Image(
            painter = painterResource(id = R.drawable.salez_logo),
            contentDescription = "Salez Logo",
            modifier = Modifier
                .size(2000.dp)
                .align(Alignment.Center)
                .offset(x = 25.dp, y = (-320).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .offset(y = 250.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "LOG IN",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = UnguTua,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("username kasir") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Putih,
                    focusedContainerColor = Putih,
                    focusedBorderColor = UnguTua,
                    unfocusedBorderColor = AbuAbu
                ),
                shape = RoundedCornerShape(50),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Putih,
                    focusedContainerColor = Putih,
                    focusedBorderColor = UnguTua,
                    unfocusedBorderColor = AbuAbu
                ),
                shape = RoundedCornerShape(50),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(8.dp))
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    errorMessage = null
                    isLoading = true
                    viewModel.login(email, password)
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                shape = RoundedCornerShape(50),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Putih, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        "Masuk",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO: Handle reset password navigation */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Oranye),
                shape = RoundedCornerShape(50),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    "Reset Password",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Lupa Password? Reset Password Disini",
                style = MaterialTheme.typography.bodyMedium.copy(color = UnguTua),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { /* TODO: Handle reset password link */ }
            )
        }
    }
}