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
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.Database.DatabaseProvider
import com.example.project_prm392.Controller.LoginController
import com.example.project_prm392.UI.LoginScreen
import com.example.project_prm392.Controller.SignUpController
import com.example.project_prm392.UI.SignUpScreen
import com.example.project_prm392.UI.theme.Project_PRM392Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            val database = DatabaseProvider.getDatabase(this)
            val repository = AppRepository(database)
            val loginController = LoginController(repository)
            val signUpController = SignUpController(repository)

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
                                    LoginScreen(navController, loginController)
                                }
                                composable("signup") {
                                    SignUpScreen(navController, signUpController)
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