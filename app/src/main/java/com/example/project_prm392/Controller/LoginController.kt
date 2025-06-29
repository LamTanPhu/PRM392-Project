package com.example.project_prm392.Controller

import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginController(private val repository: AppRepository) {
    fun login(
        username: String,
        password: String,
        onSuccess: (User) -> Unit, // Return the user object
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = repository.getUserByUsername(username)
                if (user == null) {
                    withContext(Dispatchers.Main) {
                        onError("User not found")
                    }
                } else if (user.passwordHash != password) { // Replace with proper hash check in production
                    withContext(Dispatchers.Main) {
                        onError("Incorrect password")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onSuccess(user) // Pass the user object
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Login failed: ${e.message}")
                }
            }
        }
    }
}