package com.main.proyek_salez.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.proyek_salez.data.model.User
import com.main.proyek_salez.data.repository.AuthRepository
import com.main.proyek_salez.data.repository.Result
import com.main.proyek_salez.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _loginResult = MutableLiveData<Event<Result<User>>>()
    val loginResult: LiveData<Event<Result<User>>> = _loginResult

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            _isLoggedIn.value = authRepository.isUserLoggedIn()
            _currentUser.value = authRepository.getCurrentUser()
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _loginResult.value = Event(result)
            if (result is Result.Success) {
                _currentUser.value = result.data
                _isLoggedIn.value = true
            }
        }
    }

    fun register(email: String, password: String, role: String, name: String = "", phone: String = "") {
        viewModelScope.launch {
            val result = authRepository.register(email, password, role, name, phone)
            if (result is Result.Success) {
                _currentUser.value = authRepository.getCurrentUser()
                _isLoggedIn.value = true
            }
            // Optional: Tambahkan LiveData untuk hasil registrasi jika diperlukan
        }
    }

    fun logout() {
        authRepository.logout()
        _currentUser.value = null
        _isLoggedIn.value = false

    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = authRepository.getCurrentUser()
            _isLoggedIn.value = authRepository.isUserLoggedIn()
        }
    }
}