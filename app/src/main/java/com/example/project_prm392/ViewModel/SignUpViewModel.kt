package com.example.project_prm392.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class SignUpViewModel(private val repository: AppRepository) : ViewModel() {

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _signUpSuccess = MutableStateFlow(false)
    val signUpSuccess: StateFlow<Boolean> = _signUpSuccess

    fun signUp(username: String, email: String, password: String, fullName: String, phoneNumber: String? = null) {
        viewModelScope.launch {
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                _errorMessage.value = "All fields are required"
                return@launch
            }

            val user = User(
                username = username,
                email = email,
                passwordHash = password, // Hash in production
                fullName = fullName,
                phoneNumber = phoneNumber,
                createdAt = Date(),
                role="user"
            )

            try {
                repository.insertUser(user)
                _signUpSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Signup failed: ${e.message}"
            }
        }
    }

    fun resetSignUpState() {
        _signUpSuccess.value = false
        _errorMessage.value = ""
    }
}
