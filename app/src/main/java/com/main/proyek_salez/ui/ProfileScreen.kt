package com.main.proyek_salez.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.main.proyek_salez.R
import com.main.proyek_salez.ui.theme.*
import androidx.core.net.toUri
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var isEditMode by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("DioRahmanPutra") }
    var email by remember { mutableStateOf("dio.rahman.ti123@polban.ac.id") }
    var nickname by remember { mutableStateOf("Dio") }
    var tempUsername by remember { mutableStateOf(username) }
    var tempEmail by remember { mutableStateOf(email) }
    var tempNickname by remember { mutableStateOf(nickname) }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    var tempProfilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    val hasSpacesInUsername by remember { derivedStateOf { isEditMode && tempUsername.contains(" ") } }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
    LaunchedEffect(Unit) {
        val savedUriString = sharedPreferences.getString("profile_photo_uri", null)
        profilePhotoUri = savedUriString?.toUri()
    }
    var hasPermission by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        hasPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            tempProfilePhotoUri = uri
        }
    }
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Jingga,
            Oranye,
            UnguTua
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("profile") { inclusive = true }
                    }
                },
                modifier = Modifier.padding(start = 10.dp)
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
                    .size(180.dp)
                    .offset(x = (-35).dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(140.dp))
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Putih),
                contentAlignment = Alignment.Center
            ) {
                val imageToShow = if (isEditMode) tempProfilePhotoUri else profilePhotoUri
                if (imageToShow != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageToShow),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(100.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            ProfileField(
                label = "Username",
                value = if (isEditMode) tempUsername else username,
                onValueChange = { tempUsername = it },
                isEditable = isEditMode
            )

            if (hasSpacesInUsername) {
                Text(
                    text = "username tidak boleh mengandung spasi!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Merah,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(if (hasSpacesInUsername) 0.dp else 20.dp))
            ProfileField(
                label = "Email",
                value = if (isEditMode) tempEmail else email,
                onValueChange = { tempEmail = it },
                isEditable = isEditMode
            )

            Spacer(modifier = Modifier.height(20.dp))
            ProfileField(
                label = "Nama Panggilan",
                value = if (isEditMode) tempNickname else nickname,
                onValueChange = { tempNickname = it },
                isEditable = isEditMode
            )

            Spacer(modifier = Modifier.height(30.dp))
            if (isEditMode) {
                Button(
                    onClick = {
                        if (hasPermission) {
                            pickImageLauncher.launch("image/*")
                        } else {
                            val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.READ_MEDIA_IMAGES
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }
                            permissionLauncher.launch(permission)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp)
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(50)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Oranye
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Ganti Foto",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = UnguTua,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            tempUsername = username
                            tempEmail = email
                            tempNickname = nickname
                            tempProfilePhotoUri = profilePhotoUri
                            isEditMode = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .height(40.dp)
                            .shadow(
                                elevation = 5.dp,
                                shape = RoundedCornerShape(50)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Merah
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Batalkan",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Putih,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (!tempUsername.contains(" ")) {
                                username = tempUsername
                                email = tempEmail
                                nickname = tempNickname
                                profilePhotoUri = tempProfilePhotoUri
                                sharedPreferences.edit {
                                    putString("profile_photo_uri", profilePhotoUri?.toString())
                                }
                                isEditMode = false
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .height(40.dp)
                            .shadow(
                                elevation = 5.dp,
                                shape = RoundedCornerShape(50)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasSpacesInUsername) Color.Gray else Hijau
                        ),
                        enabled = !hasSpacesInUsername,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Simpan",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Putih,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        isEditMode = true
                        tempUsername = username
                        tempEmail = email
                        tempNickname = nickname
                        tempProfilePhotoUri = profilePhotoUri
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp)
                        .shadow(
                            elevation = 5.dp,
                            shape = RoundedCornerShape(50)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Oranye
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Edit Biodata",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditable: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = UnguTua,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
        )

        if (isEditable) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    color = UnguTua
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Putih,
                    focusedContainerColor = Putih,
                    focusedBorderColor = UnguTua,
                    unfocusedBorderColor = AbuAbu
                ),
                shape = RoundedCornerShape(50)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.White, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = UnguTua,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}