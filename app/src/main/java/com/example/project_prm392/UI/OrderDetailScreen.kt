package com.example.project_prm392.UI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project_prm392.ViewModel.OrderDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Long,
    viewModel: OrderDetailViewModel = viewModel()
) {
    val items by viewModel.orderItems.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.loadOrderItems(orderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Order Details #$orderId") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Product ID: ${item.productId}")
                        Text("Quantity: ${item.quantity}")
                        Text("Unit Price: $${"%.2f".format(item.unitPrice)}")
                        Text("Subtotal: $${"%.2f".format(item.quantity * item.unitPrice)}")
                    }
                }
            }
        }
    }
}
