package com.example.project_prm392.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.model.CartItem
import com.example.project_prm392.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductListViewModel(private val repository: AppRepository) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val productList = repository.getAllProducts()
                _products.value = productList
                _filteredProducts.value = productList
            } catch (_: Exception) {
                // handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchProducts(query: String) {
        val filtered = if (query.isEmpty()) {
            _products.value
        } else {
            _products.value.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description?.contains(query, ignoreCase = true) == true ||
                        it.category?.contains(query, ignoreCase = true) == true
            }
        }
        _filteredProducts.value = filtered
    }

    fun filterByCategory(category: String) {
        val filtered = if (category == "All") {
            _products.value
        } else {
            _products.value.filter { it.category == category }
        }
        _filteredProducts.value = filtered
    }

    fun sortProducts(sortType: SortType) {
        val sorted = when (sortType) {
            SortType.NAME_ASC -> _filteredProducts.value.sortedBy { it.name }
            SortType.NAME_DESC -> _filteredProducts.value.sortedByDescending { it.name }
            SortType.PRICE_LOW_TO_HIGH -> _filteredProducts.value.sortedBy { it.price }
            SortType.PRICE_HIGH_TO_LOW -> _filteredProducts.value.sortedByDescending { it.price }
            SortType.NEWEST -> _filteredProducts.value.sortedByDescending { it.createdAt }
        }
        _filteredProducts.value = sorted
    }

    fun addToCart(
        userId: Long,
        productId: Long,
        quantity: Int = 1,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val cartItem = CartItem(userId = userId, productId = productId, quantity = quantity)
                repository.insertCartItem(cartItem)
                loadCartItemCount(userId)
                onSuccess()
            } catch (e: Exception) {
                onError("Failed to add to cart: ${e.message}")
            }
        }
    }

    fun loadCartItemCount(userId: Long) {
        viewModelScope.launch {
            try {
                val count = repository.getCartItemCount(userId)
                _cartItemCount.value = count
            } catch (_: Exception) {
                // ignore
            }
        }
    }

    fun getCategories(): List<String> {
        val categories = _products.value.mapNotNull { it.category }.distinct().sorted()
        return listOf("All") + categories
    }

    enum class SortType {
        NAME_ASC, NAME_DESC, PRICE_LOW_TO_HIGH, PRICE_HIGH_TO_LOW, NEWEST
    }
}
