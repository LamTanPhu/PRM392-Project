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
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.Database.DatabaseProvider
import com.example.project_prm392.UI.CartScreen
//import com.example.project_prm392.Controller.LoginController
import com.example.project_prm392.ViewModel.ProductListViewModel
import com.example.project_prm392.UI.LoginScreen
import com.example.project_prm392.UI.ProductDetailScreen
import com.example.project_prm392.UI.ProductListScreen
import com.example.project_prm392.UI.SignUpScreen
import com.example.project_prm392.UI.theme.Project_PRM392Theme
import com.example.project_prm392.ViewModel.CartViewModel
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
//            val loginController = LoginController(repository)
            val loginViewModel = LoginViewModel(repository)
//            val signUpController = SignUpController(repository)
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
//                                    LoginScreen(navController, loginController)
                                    LoginScreen(navController, loginViewModel)
                                }
                                composable("signup") {
                                    SignUpScreen(navController, signUpViewModel)
                                }
                                // Add the missing product_list route
                                composable("product_list") {
                                    ProductListScreen(
                                        navController = navController,
                                        viewModel = productListViewModel,
                                        currentUserId = 1L
                                    )
                                }
                                // Add other routes you might need
                                composable("cart") {
                                    val cartViewModel = CartViewModel(repository, userId = 1L)
                                    CartScreen(navController = navController, viewModel = cartViewModel)
                                }

                                composable("product_detail/{productId}") { backStackEntry ->
                                    val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
                                    if (productId != null) {
                                        ProductDetailScreen(
                                            navController = navController,
                                            productId = productId,
                                            viewModel = productDetailViewModel
                                        )
                                    } else {
                                        Text("Invalid product ID")
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
        }
    }
}