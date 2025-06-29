package com.example.project_prm392.Controller

import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class SignUpController(private val repository: AppRepository) {
    fun signUp(
        username: String,
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String? = null, // Add phoneNumber parameter with default null
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                onError("All fields are required")
                return@launch
            }

            val user = User(
                username = username,
                email = email,
                passwordHash = password, // Replace with hashed password in production
                fullName = fullName,
                phoneNumber = phoneNumber, // Now providing the phoneNumber parameter
                createdAt = Date()
            )

            try {
                repository.insertUser(user)
                onSuccess()
            } catch (e: Exception) {
                onError("Signup failed: ${e.message}")
            }
        }
    }

    // Alternative method if you want to handle phone number separately
    fun signUpWithPhone(
        username: String,
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                onError("All fields are required")
                return@launch
            }

            // Basic phone number validation (optional)
            if (phoneNumber.isNotEmpty() && !isValidPhoneNumber(phoneNumber)) {
                onError("Invalid phone number format")
                return@launch
            }

            val user = User(
                username = username,
                email = email,
                passwordHash = password, // Replace with hashed password in production
                fullName = fullName,
                phoneNumber = phoneNumber.takeIf { it.isNotEmpty() }, // Only set if not empty
                createdAt = Date()
            )

            try {
                repository.insertUser(user)
                onSuccess()
            } catch (e: Exception) {
                onError("Signup failed: ${e.message}")
            }
        }
    }

    // Simple phone number validation (you can make this more sophisticated)
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.matches(Regex("^[+]?[0-9]{10,15}$"))
    }
}