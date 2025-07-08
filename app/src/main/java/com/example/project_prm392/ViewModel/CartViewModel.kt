package com.example.project_prm392.ViewModel

import androidx.lifecycle.ViewModel
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

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState

    init {
        loadCart()
    }

    fun loadCart() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val cartItems = repository.getCartByUserId(userId)
                val itemsWithProducts = cartItems.mapNotNull { cartItem ->
                    val product = repository.getProductById(cartItem.productId)
                    if (product != null) cartItem to product else null
                }
                val total = itemsWithProducts.sumOf { (cart, product) ->
                    product.price * cart.quantity
                }

                _uiState.value = CartUiState(itemsWithProducts, total)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun removeItem(productId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(userId, productId)
            loadCart()
        }
    }
}
