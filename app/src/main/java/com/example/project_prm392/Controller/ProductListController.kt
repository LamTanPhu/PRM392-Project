package com.example.project_prm392.Controller

import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.model.Product
import com.example.project_prm392.model.CartItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductListController(private val repository: AppRepository) {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    fun loadProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            _isLoading.value = true
            try {
                val productList = repository.getAllProducts()
                _products.value = productList
                _filteredProducts.value = productList
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchProducts(query: String) {
        val filtered = if (query.isEmpty()) {
            _products.value
        } else {
            _products.value.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.description?.contains(query, ignoreCase = true) == true ||
                        product.category?.contains(query, ignoreCase = true) == true
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cartItem = CartItem(
                    userId = userId,
                    productId = productId,
                    quantity = quantity
                )
                repository.insertCartItem(cartItem)
                loadCartItemCount(userId)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Failed to add to cart: ${e.message}")
                }
            }
        }
    }

    fun loadCartItemCount(userId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val count = repository.getCartItemCount(userId)
                _cartItemCount.value = count
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getCategories(): List<String> {
        val categories = _products.value.mapNotNull { it.category }.distinct().sorted()
        return listOf("All") + categories
    }

    enum class SortType {
        NAME_ASC,
        NAME_DESC,
        PRICE_LOW_TO_HIGH,
        PRICE_HIGH_TO_LOW,
        NEWEST
    }
}