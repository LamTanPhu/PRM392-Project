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
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.Database.DatabaseProvider
import com.example.project_prm392.UI.CartScreen
import com.example.project_prm392.ViewModel.ProductListViewModel
import com.example.project_prm392.UI.LoginScreen
import com.example.project_prm392.UI.ProductDetailScreen
import com.example.project_prm392.UI.ProductListScreen
import com.example.project_prm392.UI.SignUpScreen
import com.example.project_prm392.UI.theme.Project_PRM392Theme
import com.example.project_prm392.ViewModel.CartViewModel
import com.example.project_prm392.ViewModel.CartViewModelFactory
import com.example.project_prm392.ViewModel.LoginViewModel
import com.example.project_prm392.ViewModel.ProductDetailViewModel
import com.example.project_prm392.ViewModel.SignUpViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
                                        CartScreen(navController = navController, viewModel = cartViewModel)
                                    }
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