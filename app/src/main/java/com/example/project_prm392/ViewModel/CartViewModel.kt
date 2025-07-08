package com.example.project_prm392.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.model.CartItem
import com.example.project_prm392.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<Pair<CartItem, Product>> = emptyList(),
    val total: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CartViewModel(
    private val repository: AppRepository,
    private val userId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState(isLoading = true))
    val uiState: StateFlow<CartUiState> = _uiState

    init {
        Log.d("CartViewModel", "=== CART VIEWMODEL CREATED ===")
        Log.d("CartViewModel", "UserId: $userId")
        loadCart()
    }

    private fun loadCart() {
        Log.d("CartViewModel", "=== STARTING CART LOAD ===")

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                Log.d("CartViewModel", "About to call repository.getCartByUserId($userId)")

                val cartItems = repository.getCartByUserId(userId)
                Log.d("CartViewModel", "Got ${cartItems.size} cart items")

                // Get the products for each cart item
                val itemsWithProducts = mutableListOf<Pair<CartItem, Product>>()
                var totalPrice = 0.0

                for (cartItem in cartItems) {
                    try {
                        val product = repository.getProductById(cartItem.productId)
                        if (product != null) {
                            itemsWithProducts.add(Pair(cartItem, product))
                            totalPrice += product.price * cartItem.quantity
                            Log.d("CartViewModel", "Added item: ${product.name} x${cartItem.quantity} = $${product.price * cartItem.quantity}")
                        } else {
                            Log.w("CartViewModel", "Product not found for ID: ${cartItem.productId}")
                        }
                    } catch (e: Exception) {
                        Log.e("CartViewModel", "Error getting product ${cartItem.productId}", e)
                    }
                }

                Log.d("CartViewModel", "Final cart: ${itemsWithProducts.size} items, total: $$totalPrice")

                _uiState.value = CartUiState(
                    items = itemsWithProducts,
                    total = totalPrice,
                    isLoading = false,
                    error = null
                )

            } catch (e: Exception) {
                Log.e("CartViewModel", "=== CART LOAD ERROR ===", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load cart: ${e.message}"
                )
            }
        }
    }

    fun removeItem(productId: Long) {
        viewModelScope.launch {
            try {
                repository.removeFromCart(userId, productId)
                loadCart() // Reload the cart after removal
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error removing item", e)
                _uiState.value = _uiState.value.copy(
                    error = "Failed to remove item: ${e.message}"
                )
            }
        }
    }

    fun refreshCart() {
        loadCart()
    }
}

class CartViewModelFactory(
    private val repository: AppRepository,
    private val userId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        Log.d("CartViewModelFactory", "Creating CartViewModel for userId: $userId")
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}