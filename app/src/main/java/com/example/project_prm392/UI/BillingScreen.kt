package com.example.project_prm392.UI

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_prm392.ViewModel.PaymentViewModel
import com.example.project_prm392.ViewModel.PaymentState
import com.example.project_prm392.model.CartItem
import com.example.project_prm392.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingScreen(
    navController: NavController,
    viewModel: PaymentViewModel,
    cartItems: List<Pair<CartItem, Product>>,
    totalAmount: Double
) {
    // State variables
    var address by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("VNPay") }
    val paymentState by viewModel.paymentState.collectAsState()
    val lastTotalAmount = remember { mutableStateOf(0.0) }

    // Navigate to VNPay screen when payment is successful
    LaunchedEffect(paymentState) {
        when (val state = paymentState) {
            is PaymentState.Success -> {
                val totalAmount = cartItems.sumOf { it.first.quantity * it.second.price }
                navController.navigate("vnpay_checkout/${state.orderId}/$totalAmount") {
                    popUpTo("billing/{userId}") { inclusive = true }
                }
                viewModel.resetState()
            }
            else -> Unit
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Shipping Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Payment Method: $paymentMethod")

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Total: $${"%.2f".format(totalAmount)}",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    lastTotalAmount.value = cartItems.sumOf { it.first.quantity * it.second.price }
                    viewModel.createOrder(cartItems, address, paymentMethod)
                },
                enabled = address.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pay Now")
            }


            // Optional loading state
            if (paymentState is PaymentState.Processing) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Optional error message
            if (paymentState is PaymentState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    (paymentState as PaymentState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
