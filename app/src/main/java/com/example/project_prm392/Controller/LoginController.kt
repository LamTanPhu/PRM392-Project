package com.example.project_prm392.Controller

import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginController(private val repository: AppRepository) {
    fun login(
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = repository.getUserByUsername(username)
            if (user == null) {
                onError("User not found")
            } else if (user.passwordHash != password) { // Replace with proper hash check in production
                onError("Incorrect password")
            } else {
                onSuccess()
            }
        }
    }
}