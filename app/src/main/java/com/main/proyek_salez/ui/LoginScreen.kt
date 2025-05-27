package com.main.proyek_salez.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.main.proyek_salez.R
import com.main.proyek_salez.data.model.User
import com.main.proyek_salez.data.repository.Result
import com.main.proyek_salez.data.viewmodel.AuthViewModel
import androidx.activity.compose.BackHandler
import com.main.proyek_salez.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = true) {
        // Tidak ada aksi, mencegah kembali
    }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val loginResult by viewModel.loginResult.observeAsState()

    LaunchedEffect(loginResult) {
        Log.d("LoginScreen", "Login result changed: $loginResult")
        loginResult?.getContentIfNotHandled()?.let { result ->
            isLoading = false
            when (result) {
                is Result.Success -> {
                    Log.d("LoginScreen", "Login success for user: ${result.data.email}, role: ${result.data.role}")
                    errorMessage = null
                    onLoginSuccess(result.data)
                }
                is Result.Error -> {
                    errorMessage = when {
                        result.message?.contains("PERMISSION_DENIED") == true ->
                            "Gagal login: Tidak ada izin untuk mengakses data pengguna."
                        result.message?.contains("Dokumen pengguna tidak ditemukan") == true ->
                            "Akun belum terdaftar di sistem. Silakan registrasi."
                        result.message?.contains("The email address is badly formatted") == true ->
                            "Email tidak valid. Silakan masukkan email yang benar."
                        result.message?.contains("The supplied auth credential is incorrect") == true ->
                            "Email atau kata sandi salah. Silakan coba lagi."
                        result.message?.contains("Peran tidak valid") == true ->
                            "Peran akun tidak valid: ${result.message}"
                        else -> result.message ?: "Gagal login: Terjadi kesalahan."
                    }
                    Log.e("LoginScreen", "Error set: $errorMessage")
                }
            }
        }
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Putih, Jingga, UnguTua)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = gradientBackground)
    ) {
        IconButton(
            onClick = { /* Tidak ada aksi */ },
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
                .size(800.dp)
                .scale(1.5f)
                .align(Alignment.Center)
                .offset(x = 20.dp, y = (-200).dp)
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
                label = { Text("Username") },
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
                label = { Text("Password") },
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
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    errorMessage = null
                    isLoading = true
                    Log.d("LoginScreen", "Login button clicked with email: $email")
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
        }
    }
}