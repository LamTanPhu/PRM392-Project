package com.example.project_prm392.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_prm392.DAO.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VNPayResultViewModel(private val repository: AppRepository) : ViewModel() {
    private val _responseCode = MutableStateFlow("?")
    val responseCode: StateFlow<String> = _responseCode

    private val _transactionRef = MutableStateFlow("?")
    val transactionRef: StateFlow<String> = _transactionRef

    private val _userId = MutableStateFlow<Long?>(null)
    val userId: StateFlow<Long?> = _userId

    fun setResult(code: String, txn: String) {
        _responseCode.value = code
        _transactionRef.value = txn

        viewModelScope.launch {
            val orderId = txn.toLongOrNull()

            orderId?.let {
                val userIdFromOrder = repository.getUserIdByOrderId(it)
                _userId.value = userIdFromOrder

                if (code == "00") {
                    repository.updateOrderStatus(it, "Paid")
                }
            }
        }
    }

}

// 👇 Factory embedded in the same file
class VNPayResultViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VNPayResultViewModel::class.java)) {
            return VNPayResultViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
