package com.example.project_prm392.UI

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project_prm392.ViewModel.VNPayViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VNPayScreen(navController: NavController, orderId: Long, totalAmount: Double) {
    val context = LocalContext.current
    val viewModel: VNPayViewModel = viewModel()
    val redirectUrl by viewModel.redirectUrl.collectAsState()

    LaunchedEffect(orderId, totalAmount) {
        viewModel.createVNPayUrl(
            orderId = orderId,
            amount = totalAmount,
            returnUrl = "https://sandbox.vnpayment.vn/return"
        )
    }

    if (redirectUrl != null) {
        LaunchedEffect(redirectUrl) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl))
            context.startActivity(intent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("VNPay Checkout") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Redirecting to VNPay...", style = MaterialTheme.typography.headlineSmall)
            LinearProgressIndicator(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp))
        }
    }
}

