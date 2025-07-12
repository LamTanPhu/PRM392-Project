package com.example.project_prm392

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.Database.DatabaseProvider
import com.example.project_prm392.UI.BillingScreen
import com.example.project_prm392.UI.CartScreen
import com.example.project_prm392.ViewModel.ProductListViewModel
import com.example.project_prm392.UI.LoginScreen
import com.example.project_prm392.UI.MapScreen
import com.example.project_prm392.UI.OrderDetailScreen
import com.example.project_prm392.UI.ProductDetailScreen
import com.example.project_prm392.UI.ProductListScreen
import com.example.project_prm392.UI.SignUpScreen
import com.example.project_prm392.UI.VNPayResultScreen
import com.example.project_prm392.UI.VNPayScreen
import com.example.project_prm392.UI.theme.Project_PRM392Theme
import com.example.project_prm392.ViewModel.CartViewModel
import com.example.project_prm392.ViewModel.CartViewModelFactory
import com.example.project_prm392.ViewModel.LoginViewModel
import com.example.project_prm392.ViewModel.MapViewModel
import com.example.project_prm392.ViewModel.OrdersViewModel
import com.example.project_prm392.ViewModel.PaymentState
import com.example.project_prm392.ViewModel.PaymentViewModel
import com.example.project_prm392.ViewModel.PaymentViewModelFactory
import com.example.project_prm392.ViewModel.ProductDetailViewModel
import com.example.project_prm392.ViewModel.SignUpViewModel
import com.example.project_prm392.ViewModel.VNPayResultViewModel
import com.example.project_prm392.UI.OrdersScreen
import com.example.project_prm392.ViewModel.OrderDetailViewModel
import com.example.project_prm392.ViewModel.OrderDetailViewModelFactory
import com.example.project_prm392.ViewModel.VNPayResultViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val deepLinkUri = intent?.data

        try {
            val database = DatabaseProvider.getDatabase(this)
            val repository = AppRepository(database)
            val loginViewModel = LoginViewModel(repository)
            val productListViewModel = ProductListViewModel(repository)
            val signUpViewModel = SignUpViewModel(repository)
            val productDetailViewModel = ProductDetailViewModel(repository)

            setContent {
                Project_PRM392Theme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val navController = rememberNavController()

                            LaunchedEffect(deepLinkUri) {
                                deepLinkUri?.let { uri ->
                                    if (
                                        uri.scheme == "projectprmpp" &&
                                        uri.host == "vnpay" &&
                                        uri.path?.startsWith("/return") == true
                                    ) {
                                        val responseCode = uri.getQueryParameter("vnp_ResponseCode") ?: "?"
                                        val txnRef = uri.getQueryParameter("vnp_TxnRef") ?: "?"
                                        navController.navigate("vnpay_result/$responseCode/$txnRef")
                                    }
                                }
                            }

                            NavHost(navController = navController, startDestination = "login") {
                                composable("login") {
                                    LoginScreen(navController, loginViewModel)
                                }
                                composable("signup") {
                                    SignUpScreen(navController, signUpViewModel)
                                }

                                // Updated to accept userId parameter
                                composable(
                                    "product_list/{userId}",
                                    arguments = listOf(navArgument("userId") { type = NavType.LongType })
                                ) { backStackEntry ->
                                    val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
                                    ProductListScreen(
                                        navController = navController,
                                        viewModel = productListViewModel,
                                        currentUserId = userId
                                    )
                                }

                                // Fixed cart navigation with better error handling
                                composable(
                                    "cart/{userId}",
                                    arguments = listOf(navArgument("userId") { type = NavType.LongType })
                                ) { backStackEntry ->
                                    val userId = backStackEntry.arguments?.getLong("userId") ?: 0L

                                    if (userId <= 0) {
                                        // Handle invalid userId - redirect to login
                                        LaunchedEffect(Unit) {
                                            navController.navigate("login") {
                                                popUpTo("cart/{userId}") { inclusive = true }
                                            }
                                        }
                                        Text("Invalid user ID. Redirecting to login...")
                                    } else {
                                        val cartViewModel: CartViewModel = viewModel(
                                            factory = CartViewModelFactory(repository, userId)
                                        )
                                        CartScreen(navController = navController, viewModel = cartViewModel, userId = userId)

                                    }
                                }
                                composable("map") {
                                    MapScreen(navController)
                                }


                                composable(
                                    "product_detail/{productId}",
                                    arguments = listOf(
                                        navArgument("productId") { type = NavType.LongType }
                                    )
                                ) { backStackEntry ->
                                    val productId = backStackEntry.arguments?.getLong("productId") ?: 0L

                                    if (productId <= 0) {
                                        Text("Invalid product ID")
                                    } else {
                                        ProductDetailScreen(
                                            navController = navController,
                                            productId = productId,
                                            viewModel = productDetailViewModel
                                        )
                                    }
                                }
                                // Billing screen (payment)
                                composable(
                                    "billing/{userId}",
                                    arguments = listOf(navArgument("userId") { type = NavType.LongType })
                                ) { backStackEntry ->
                                    val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
                                    if (userId <= 0) {
                                        Text("Invalid user ID")
                                    } else {
                                        val cartViewModel = viewModel<CartViewModel>(
                                            factory = CartViewModelFactory(repository, userId)
                                        )
                                        val paymentViewModel = viewModel<PaymentViewModel>(
                                            factory = PaymentViewModelFactory(repository, userId)
                                        )

                                        val cartState = cartViewModel.uiState.collectAsState().value
                                        val paymentState = paymentViewModel.paymentState.collectAsState().value

                                        // Navigate to VNPay screen on success
                                        LaunchedEffect(paymentState) {
                                            when (val state = paymentState) {
                                                is PaymentState.Success -> {
                                                    val totalAmount = cartState.items.sumOf { it.first.quantity * it.second.price }
                                                    navController.navigate("vnpay_checkout/${state.orderId}/${totalAmount.toFloat()}") {
                                                        popUpTo("billing/{userId}") { inclusive = true }
                                                    }
                                                    paymentViewModel.resetState()
                                                }
                                                else -> Unit
                                            }
                                        }


                                        if (!cartState.isLoading && cartState.items.isNotEmpty()) {
                                            BillingScreen(
                                                navController = navController,
                                                viewModel = paymentViewModel,
                                                cartItems = cartState.items,
                                                totalAmount = cartState.total
                                            )
                                        } else {
                                            Text("No items to checkout.")
                                        }
                                    }
                                }

                                // VNPay confirmation screen
                                composable(
                                    "vnpay_checkout/{orderId}/{totalAmount}",
                                    arguments = listOf(
                                        navArgument("orderId") { type = NavType.LongType },
                                        navArgument("totalAmount") { type = NavType.FloatType }
                                    )
                                ) { backStackEntry ->
                                    val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
                                    val totalAmount = backStackEntry.arguments?.getFloat("totalAmount")?.toDouble() ?: 0.0

                                    if (orderId > 0 && totalAmount > 0.0) {
                                        VNPayScreen(navController = navController, orderId = orderId, totalAmount = totalAmount)
                                    } else {
                                        Text("Invalid order ID or amount")
                                    }
                                }



                                composable(
                                    "vnpay_result/{responseCode}/{txnRef}",
                                    arguments = listOf(
                                        navArgument("responseCode") { type = NavType.StringType },
                                        navArgument("txnRef") { type = NavType.StringType }
                                    )
                                ) { backStackEntry ->
                                    val viewModel: VNPayResultViewModel = viewModel(
                                        factory = VNPayResultViewModelFactory(repository)
                                    )
                                    val code = backStackEntry.arguments?.getString("responseCode") ?: "?"
                                    val txn = backStackEntry.arguments?.getString("txnRef") ?: "?"

                                    LaunchedEffect(Unit) {
                                        viewModel.setResult(code, txn)
                                    }

                                    VNPayResultScreen(viewModel = viewModel, navController = navController)
                                }

                                composable(
                                    "orders/{userId}",
                                    arguments = listOf(navArgument("userId") { type = NavType.LongType })
                                ) { backStackEntry ->
                                    val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
                                    val ordersViewModel: OrdersViewModel = viewModel(
                                        factory = OrdersViewModel.Factory(repository)
                                    )

                                    LaunchedEffect(userId) {
                                        ordersViewModel.loadOrders(userId)
                                    }

                                    OrdersScreen(
                                        userId = userId,
                                        repository = repository,
                                        navController = navController
                                    )
                                }

                                composable(
                                    "order_detail/{orderId}",
                                    arguments = listOf(navArgument("orderId") { type = NavType.LongType })
                                ) { backStackEntry ->
                                    val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
                                    val orderDetailViewModel: OrderDetailViewModel = viewModel(
                                        factory = OrderDetailViewModelFactory(repository)
                                    )

                                    OrderDetailScreen(orderId = orderId, viewModel = orderDetailViewModel)
                                }



                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle initialization errors gracefully
            setContent {
                Project_PRM392Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Text("Error initializing app: ${e.message}")
                    }
                }
            }
        }
    }
}