package com.example.project_prm392.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.model.Product
import com.example.project_prm392.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AdminDashboardViewModel(
    private val repository: AppRepository,
    private val userId: Long
) : ViewModel() {

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            val user = repository.getUserByUserId(userId)
            _isAdmin.value = user?.role == "admin"
            if (_isAdmin.value) {
                loadProducts()
                loadUsers()
            }
            _isLoading.value = false
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            _products.value = repository.getAllProducts()
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _users.value = repository.getAllUser()
        }
    }

    fun createProduct(name: String, price: Double, description: String, stock: Int, imageUrl: String, category: String) {
        viewModelScope.launch {
            val product = Product(
                name = name,
                price = price,
                description = description,
                stockQuantity = stock,
                imageUrl = imageUrl,
                category = category,
                createdAt = Date()
            )
            repository.insertProduct(product)
            loadProducts()
        }
    }

    fun createUser(username: String, email: String, passwordHash: String, fullName: String, phoneNumber: String, role: String) {
        viewModelScope.launch {
            val user = User(
                username = username,
                email = email,
                passwordHash = passwordHash,
                fullName = fullName,
                phoneNumber = phoneNumber,
                role = role,
                createdAt = Date()
            )
            repository.insertUser(user)
            loadUsers()
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
            loadProducts()
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
            loadUsers()
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            repository.deleteUserById(userId)
            loadUsers()
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProductById(productId)
            loadProducts()
        }
    }

    class Factory(
        private val repository: AppRepository,
        private val userId: Long
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdminDashboardViewModel::class.java)) {
                return AdminDashboardViewModel(repository, userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
