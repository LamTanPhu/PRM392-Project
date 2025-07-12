package com.example.project_prm392.UI

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_prm392.ViewModel.VNPayResultViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VNPayResultScreen(
    viewModel: VNPayResultViewModel,
    navController: NavController
) {
    val code by viewModel.responseCode.collectAsState()
    val txn by viewModel.transactionRef.collectAsState()
    val userId by viewModel.userId.collectAsState()

    fun getResponseMessage(code: String): String = when (code) {
        "00" -> "Payment successful"
        "07" -> "Suspected fraud"
        "09" -> "Transaction declined"
        "10" -> "Invalid card"
        "24" -> "User canceled the transaction"
        "51" -> "Insufficient funds"
        "65" -> "Exceeds withdrawal limit"
        else -> "Unknown payment status"
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("VNPay Result") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("Payment Status:", style = MaterialTheme.typography.titleMedium)
            Text(getResponseMessage(code), style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Order Code Reference:", style = MaterialTheme.typography.titleMedium)
            Text(txn, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))

            if (userId != null) {
                Button(
                    onClick = {
                        navController.navigate("product_list/${userId}") {
                            popUpTo("vnpay_result/$code/$txn") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Product List")
                }
            } else {
                Text("Loading user info...", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

