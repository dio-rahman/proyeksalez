package com.salez.kasir.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.salez.kasir.data.models.User
import com.salez.kasir.data.models.UserRole
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Login failed: User is null"))

            val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()

            // Convert map to User object manually to handle Timestamp conversion
            if (userDoc.exists()) {
                val data = userDoc.data
                if (data != null) {
                    val user = User(
                        userId = userDoc.id,
                        name = data["name"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        phone = data["phone"] as? String ?: "",
                        role = try {
                            UserRole.valueOf(data["role"] as? String ?: UserRole.CASHIER.name)
                        } catch (e: Exception) {
                            UserRole.CASHIER
                        },
                        // Handle createdAt conversion properly
                        createdAt = when (val timestamp = data["createdAt"]) {
                            is com.google.firebase.Timestamp -> timestamp.toDate().time
                            is Long -> timestamp
                            else -> System.currentTimeMillis()
                        }
                    )
                    Result.success(user)
                } else {
                    Result.failure(Exception("User data is null"))
                }
            } else {
                Result.failure(Exception("User document not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // For backward compatibility with existing code
    suspend fun loginWithRoleCheck(email: String, password: String): Result<User> {
        return login(email, password)
    }

    suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        phone: String,
        role: UserRole
    ): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Registration failed: User is null"))

            val newUser = User(
                userId = firebaseUser.uid,
                name = name,
                email = email,
                phone = phone,
                role = role
            )

            firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        try {
            val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()

            // Sama seperti di fungsi login, kita handle conversion manual
            if (userDoc.exists()) {
                val data = userDoc.data
                if (data != null) {
                    return User(
                        userId = userDoc.id,
                        name = data["name"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        phone = data["phone"] as? String ?: "",
                        role = try {
                            UserRole.valueOf(data["role"] as? String ?: UserRole.CASHIER.name)
                        } catch (e: Exception) {
                            UserRole.CASHIER
                        },
                        // Handle createdAt conversion properly
                        createdAt = when (val timestamp = data["createdAt"]) {
                            is com.google.firebase.Timestamp -> timestamp.toDate().time
                            is Long -> timestamp
                            else -> System.currentTimeMillis()
                        }
                    )
                }
            }
        } catch (e: Exception) {
            // Log error jika diperlukan
        }
        return null
    }

    fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}