package com.example.project_prm392.UI

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.project_prm392.ViewModel.AdminDashboardViewModel
import com.example.project_prm392.model.Product
import com.example.project_prm392.model.User
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminDashboardViewModel
) {
    val isAdmin by viewModel.isAdmin.collectAsState()
    val products by viewModel.products.collectAsState()
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var viewProduct by remember { mutableStateOf<Product?>(null) }
    var viewUser by remember { mutableStateOf<User?>(null) }

    var showCreateProductDialog by remember { mutableStateOf(false) }
    var showCreateUserDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Dashboard") }) },
        floatingActionButton = {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FloatingActionButton(onClick = { showCreateProductDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Create Product")
                }
                FloatingActionButton(onClick = { showCreateUserDialog = true }) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Create User")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                !isAdmin -> Text("Access Denied", Modifier.align(Alignment.Center))
                else -> Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // PRODUCTS
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("📦 Products", style = MaterialTheme.typography.headlineSmall)
                            Divider(Modifier.padding(vertical = 8.dp))
                            LazyColumn(Modifier.heightIn(max = 250.dp)) {
                                items(products) { product ->
                                    ListItem(
                                        leadingContent = {
                                            product.imageUrl?.let {
                                                AsyncImage(
                                                    model = File(it),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(48.dp)
                                                )
                                            }
                                        },
                                        headlineContent = { Text(product.name) },
                                        supportingContent = { Text("$${product.price}") },
                                        trailingContent = {
                                            Row {
                                                IconButton(onClick = { viewProduct = product }) {
                                                    Icon(Icons.Default.Visibility, contentDescription = "View")
                                                }
                                                IconButton(onClick = { selectedProduct = product }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                                }
                                                IconButton(onClick = { viewModel.deleteProduct(product.product_id) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // USERS
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("👥 Users", style = MaterialTheme.typography.headlineSmall)
                            Divider(Modifier.padding(vertical = 8.dp))
                            LazyColumn(Modifier.heightIn(max = 250.dp)) {
                                items(users) { user ->
                                    ListItem(
                                        headlineContent = { Text(user.username) },
                                        supportingContent = { Text(user.role) },
                                        trailingContent = {
                                            Row {
                                                IconButton(onClick = { viewUser = user }) {
                                                    Icon(Icons.Default.Visibility, contentDescription = "View")
                                                }
                                                IconButton(onClick = { selectedUser = user }) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                                }
                                                IconButton(onClick = { viewModel.deleteUser(user.user_id) }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // VIEW PRODUCT
    viewProduct?.let { product ->
        AlertDialog(
            onDismissRequest = { viewProduct = null },
            title = { Text("Product Details") },
            text = {
                Column {
                    Text("Name: ${product.name}")
                    Text("Price: $${product.price}")
                    Text("Stock: ${product.stockQuantity}")
                    Text("Description: ${product.description ?: "-"}")
                    Text("Category: ${product.category ?: "-"}")
                }
            },
            confirmButton = {
                TextButton(onClick = { viewProduct = null }) { Text("Close") }
            }
        )
    }

    // VIEW USER
    viewUser?.let { user ->
        AlertDialog(
            onDismissRequest = { viewUser = null },
            title = { Text("User Details") },
            text = {
                Column {
                    Text("Username: ${user.username}")
                    Text("Email: ${user.email}")
                    Text("Full Name: ${user.fullName ?: "-"}")
                    Text("Phone: ${user.phoneNumber ?: "-"}")
                    Text("Role: ${user.role}")
                }
            },
            confirmButton = {
                TextButton(onClick = { viewUser = null }) { Text("Close") }
            }
        )
    }

    // CREATE PRODUCT
    if (showCreateProductDialog) {
        var name by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var stock by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var imagePath by remember { mutableStateOf("") }

        val context = LocalContext.current
        val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val fileName = "product_${System.currentTimeMillis()}.jpg"
                val input = context.contentResolver.openInputStream(it)
                val file = File(context.filesDir, fileName)
                input?.use { i -> file.outputStream().use { o -> i.copyTo(o) } }
                imagePath = file.absolutePath
            }
        }

        AlertDialog(
            onDismissRequest = { showCreateProductDialog = false },
            title = { Text("Create Product") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Name") })
                    OutlinedTextField(price, { price = it }, label = { Text("Price") })
                    OutlinedTextField(description, { description = it }, label = { Text("Description") })
                    OutlinedTextField(stock, { stock = it }, label = { Text("Stock") })
                    OutlinedTextField(category, { category = it }, label = { Text("Category") })
                    Button(onClick = { imagePicker.launch("image/*") }) {
                        Text("Pick Image")
                    }
                    if (imagePath.isNotEmpty()) Text("Selected: ${File(imagePath).name}")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.createProduct(
                        name,
                        price.toDoubleOrNull() ?: 0.0,
                        description,
                        stock.toIntOrNull() ?: 0,
                        imagePath,
                        category
                    )
                    showCreateProductDialog = false
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateProductDialog = false }) { Text("Cancel") }
            }
        )
    }

    // CREATE USER
    if (showCreateUserDialog) {
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var fullName by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("user") }

        AlertDialog(
            onDismissRequest = { showCreateUserDialog = false },
            title = { Text("Create User") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(username, { username = it }, label = { Text("Username") })
                    OutlinedTextField(email, { email = it }, label = { Text("Email") })
                    OutlinedTextField(password, { password = it }, label = { Text("Password") })
                    OutlinedTextField(fullName, { fullName = it }, label = { Text("Full Name") })
                    OutlinedTextField(phone, { phone = it }, label = { Text("Phone") })
                    OutlinedTextField(role, { role = it }, label = { Text("Role") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.createUser(
                        username,
                        email,
                        password,
                        fullName,
                        phone,
                        role
                    )
                    showCreateUserDialog = false
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateUserDialog = false }) { Text("Cancel") }
            }
        )
    }

    // UPDATE PRODUCT
    selectedProduct?.let { product ->
        var name by remember { mutableStateOf(product.name) }
        var price by remember { mutableStateOf(product.price.toString()) }
        var description by remember { mutableStateOf(product.description ?: "") }
        var stock by remember { mutableStateOf(product.stockQuantity.toString()) }
        var category by remember { mutableStateOf(product.category ?: "") }
        var imageUrl by remember { mutableStateOf(product.imageUrl ?: "") }

        val context = LocalContext.current
        val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val fileName = "product_update_${System.currentTimeMillis()}.jpg"
                val input = context.contentResolver.openInputStream(it)
                val file = File(context.filesDir, fileName)
                input?.use { i -> file.outputStream().use { o -> i.copyTo(o) } }
                imageUrl = file.absolutePath
            }
        }

        AlertDialog(
            onDismissRequest = { selectedProduct = null },
            title = { Text("Update Product") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Name") })
                    OutlinedTextField(price, { price = it }, label = { Text("Price") })
                    OutlinedTextField(description, { description = it }, label = { Text("Description") })
                    OutlinedTextField(stock, { stock = it }, label = { Text("Stock") })
                    OutlinedTextField(category, { category = it }, label = { Text("Category") })
                    Button(onClick = { imagePicker.launch("image/*") }) {
                        Text("Change Image")
                    }
                    if (imageUrl.isNotEmpty()) Text("Selected: ${File(imageUrl).name}")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateProduct(
                        product.copy(
                            name = name,
                            price = price.toDoubleOrNull() ?: 0.0,
                            description = description,
                            stockQuantity = stock.toIntOrNull() ?: 0,
                            category = category,
                            imageUrl = imageUrl
                        )
                    )
                    selectedProduct = null
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProduct = null }) { Text("Cancel") }
            }
        )
    }

    // UPDATE USER
    selectedUser?.let { user ->
        var username by remember { mutableStateOf(user.username) }
        var email by remember { mutableStateOf(user.email) }
        var fullName by remember { mutableStateOf(user.fullName ?: "") }
        var phone by remember { mutableStateOf(user.phoneNumber ?: "") }
        var role by remember { mutableStateOf(user.role) }

        AlertDialog(
            onDismissRequest = { selectedUser = null },
            title = { Text("Update User") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(username, { username = it }, label = { Text("Username") })
                    OutlinedTextField(email, { email = it }, label = { Text("Email") })
                    OutlinedTextField(fullName, { fullName = it }, label = { Text("Full Name") })
                    OutlinedTextField(phone, { phone = it }, label = { Text("Phone") })
                    OutlinedTextField(role, { role = it }, label = { Text("Role") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateUser(
                        user.copy(
                            username = username,
                            email = email,
                            fullName = fullName,
                            phoneNumber = phone,
                            role = role
                        )
                    )
                    selectedUser = null
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUser = null }) { Text("Cancel") }
            }
        )
    }
}
