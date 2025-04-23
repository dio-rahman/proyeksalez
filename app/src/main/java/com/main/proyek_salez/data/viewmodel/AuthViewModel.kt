package com.main.proyek_salez.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.entities.User
import com.main.proyek_salez.data.entities.UserRole
import com.main.proyek_salez.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    private val _registerResult = MutableLiveData<Result<User>>()
    val registerResult: LiveData<Result<User>> = _registerResult

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    // Check role saat login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.loginWithRoleCheck(email, password)
            _loginResult.value = result
        }
    }

    // Register user baru
    fun registerUser(email: String, password: String, name: String, phone: String, role: UserRole) {
        viewModelScope.launch {
            val result = authRepository.registerUser(email, password, name, phone, role)
            _registerResult.value = result
        }
    }

    // Dapatkan user saat ini
    fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = authRepository.getCurrentUser()
        }
    }

    // Logout
    fun logout() {
        authRepository.logout()
        _currentUser.value = null
    }

    companion object
}