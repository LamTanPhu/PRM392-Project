package com.example.project_prm392.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.model.Order
import com.example.project_prm392.model.OrderItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

sealed class PaymentState {
    object Idle : PaymentState()
    object Processing : PaymentState()
    data class Success(val orderId: Long) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

class PaymentViewModel(private val repository: AppRepository, private val userId: Long) : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState

    fun createOrder(
        items: List<Pair<com.example.project_prm392.model.CartItem, com.example.project_prm392.model.Product>>,
        address: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            try {
                _paymentState.value = PaymentState.Processing

                val total = items.sumOf { it.first.quantity * it.second.price }
                val newOrder = Order(
                    userId = userId,
                    address = address,
                    totalAmount = total,
                    status = "Pending",
                    paymentMethod = paymentMethod,
                    orderDate = Date()
                )

                repository.insertOrder(newOrder)
                val createdOrder = repository.getLatestOrderByUser(userId)

                items.forEach { (cartItem, product) ->
                    val orderItem = OrderItem(
                        orderId = createdOrder.order_id,
                        productId = product.product_id,
                        quantity = cartItem.quantity,
                        unitPrice = product.price
                    )
                    repository.insertOrderItem(orderItem)
                }

                _paymentState.value = PaymentState.Success(createdOrder.order_id)
            } catch (e: Exception) {
                _paymentState.value = PaymentState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _paymentState.value = PaymentState.Idle
    }
}
class PaymentViewModelFactory(
    private val repository: AppRepository,
    private val userId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
