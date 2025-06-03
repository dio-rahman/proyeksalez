package com.main.proyek_salez.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.main.proyek_salez.data.model.User
import com.main.proyek_salez.data.model.UserRole
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            Log.d("AuthRepository", "Mencoba login dengan email: $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user == null) {
                Log.e("AuthRepository", "Gagal mendapatkan UID pengguna")
                return Result.Error("Gagal mendapatkan ID pengguna")
            }
            val userId = result.user!!.uid
            Log.d("AuthRepository", "Login berhasil, UID: $userId, Authenticated: ${auth.currentUser != null}")

            val userDoc = firestore.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                Log.e("AuthRepository", "Dokumen pengguna tidak ditemukan untuk UID: $userId")
                return Result.Error("Dokumen pengguna tidak ditemukan di Firestore")
            }
            Log.d("AuthRepository", "Dokumen ditemukan: ${userDoc.data}")

            if (userDoc.getString("role") == null) {
                Log.e("AuthRepository", "Field 'role' tidak ditemukan di dokumen")
                return Result.Error("Peran pengguna tidak ditemukan")
            }
            val role = userDoc.getString("role")!!
            Log.d("AuthRepository", "Role ditemukan: $role")

            val createdAt = when (val createdAtValue = userDoc.get("createdAt")) {
                is Long -> createdAtValue
                is String -> {
                    try {
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(createdAtValue)?.time
                            ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        Log.e("AuthRepository", "Gagal parsing createdAt: $createdAtValue", e)
                        System.currentTimeMillis()
                    }
                }
                is Timestamp -> createdAtValue.toDate().time
                else -> {
                    Log.w("AuthRepository", "createdAt tidak valid atau null, menggunakan default")
                    System.currentTimeMillis()
                }
            }

            val user = User(
                userId = userId,
                name = userDoc.getString("name") ?: "",
                email = email,
                phone = userDoc.getString("phone") ?: "",
                role = when (role) {
                    "MANAGER" -> UserRole.MANAGER
                    "CASHIER" -> UserRole.CASHIER
                    "CHEF" -> UserRole.CHEF
                    else -> {
                        Log.e("AuthRepository", "Peran tidak valid: $role")
                        return Result.Error("Peran tidak valid: $role")
                    }
                },
                createdAt = createdAt
            )
            Log.d("AuthRepository", "User dibuat: $user")
            Result.Success(user)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Gagal login: ${e.message}", e)
            Result.Error("Gagal login: ${e.message}")
        }
    }

    suspend fun register(email: String, password: String, role: String, name: String = "", phone: String = ""): Result<Unit> {
        return try {
            Log.d("AuthRepository", "Mencoba registrasi dengan email: $email, role: $role")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            if (result.user == null) {
                Log.e("AuthRepository", "Gagal mendapatkan UID pengguna")
                return Result.Error("Gagal mendapatkan ID pengguna")
            }
            val userId = result.user!!.uid
            firestore.collection("users").document(userId).set(
                mapOf(
                    "userId" to userId,
                    "email" to email,
                    "role" to role,
                    "name" to name,
                    "phone" to phone,
                    "createdAt" to Timestamp.now()
                )
            ).await()
            Log.d("AuthRepository", "Registrasi berhasil, UID: $userId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Gagal registrasi: ${e.message}", e)
            Result.Error("Gagal registrasi: ${e.message}")
        }
    }

    suspend fun getCurrentUser(): User? {
        if (auth.currentUser == null) {
            Log.e("AuthRepository", "Tidak ada pengguna yang login")
            return null
        }
        val userId = auth.currentUser!!.uid
        Log.d("AuthRepository", "Mengambil pengguna saat ini, UID: $userId")

        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                Log.e("AuthRepository", "Dokumen pengguna tidak ditemukan untuk UID: $userId")
                return null
            }
            Log.d("AuthRepository", "Dokumen ditemukan: ${userDoc.data}")
            if (userDoc.getString("role") == null) {
                Log.e("AuthRepository", "Field 'role' tidak ditemukan")
                return null
            }
            val role = userDoc.getString("role")!!
            Log.d("AuthRepository", "Role ditemukan: $role")

            val createdAt = when (val createdAtValue = userDoc.get("createdAt")) {
                is Long -> createdAtValue
                is String -> {
                    try {
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(createdAtValue)?.time
                            ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        Log.e("AuthRepository", "Gagal parsing createdAt: $createdAtValue", e)
                        System.currentTimeMillis()
                    }
                }
                is Timestamp -> createdAtValue.toDate().time
                else -> {
                    Log.w("AuthRepository", "createdAt tidak valid atau null, menggunakan default")
                    System.currentTimeMillis()
                }
            }

            User(
                userId = userId,
                name = userDoc.getString("name") ?: "",
                email = userDoc.getString("email") ?: "",
                phone = userDoc.getString("phone") ?: "",
                role = when (role) {
                    "MANAGER" -> UserRole.MANAGER
                    "CASHIER" -> UserRole.CASHIER
                    "CHEF" -> UserRole.CHEF
                    else -> {
                        Log.e("AuthRepository", "Peran tidak valid: $role")
                        return null
                    }
                },
                createdAt = createdAt
            )
        } catch (e: Exception) {
            Log.e("AuthRepository", "Gagal mengambil pengguna saat ini: ${e.message}", e)
            return null
        }
    }

    fun isUserLoggedIn(): Boolean {
        val isLoggedIn = auth.currentUser != null
        Log.d("AuthRepository", "isUserLoggedIn: $isLoggedIn")
        return isLoggedIn
    }

    fun logout() {
        Log.d("AuthRepository", "Logout dipanggil")
        auth.signOut()
    }
}