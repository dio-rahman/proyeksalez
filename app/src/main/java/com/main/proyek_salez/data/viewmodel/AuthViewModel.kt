package com.main.proyek_salez.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.entities.User
import com.main.proyek_salez.data.entities.UserRole
import com.main.proyek_salez.data.repository.AuthRepository
import com.main.proyek_salez.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Event<Result<User>>>()
    val loginResult: LiveData<Event<Result<User>>> = _loginResult

    private val _registerResult = MutableLiveData<Event<Result<User>>>()
    val registerResult: LiveData<Event<Result<User>>> = _registerResult

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    // Check role saat login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.loginWithRoleCheck(email, password)
            _loginResult.value = Event(result)
        }
    }

    // Register user baru
    fun registerUser(email: String, password: String, name: String, phone: String, role: UserRole) {
        viewModelScope.launch {
            val result = authRepository.registerUser(email, password, name, phone, role)
            _registerResult.value = Event(result)
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

    fun clearLoginState() {
        _loginResult.value = Event(Result.failure(Exception("State cleared")))
    }

    companion object
}