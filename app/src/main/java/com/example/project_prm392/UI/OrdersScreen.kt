// File: UI/OrdersScreen.kt
package com.example.project_prm392.UI

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.model.Order
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(userId: Long, repository: AppRepository, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        coroutineScope.launch {
            orders = repository.getOrdersByUserId(userId)
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Orders") })
        }
    ) { padding ->
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (orders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Order #${order.order_id}", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Text("Total: $${"%.2f".format(order.totalAmount)}")
                            Text("Status: ${order.status}")
                            Text("Date: ${order.orderDate}")
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    navController.navigate("order_detail/${order.order_id}")
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("View Details")
                            }
                        }
                    }
                }
            }
        }
    }
}
