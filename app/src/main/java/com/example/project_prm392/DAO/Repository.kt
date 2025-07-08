package com.example.project_prm392.DAO

import com.example.project_prm392.Database.ElectronicStoreDatabase
import com.example.project_prm392.model.*

class AppRepository(private val database: ElectronicStoreDatabase) {

    // User operations
    suspend fun insertUser(user: User) = database.userDao().insert(user)
    suspend fun getUserByUsername(username: String) = database.userDao().getByUsername(username)

    // Product operations
    suspend fun insertProduct(product: Product) = database.productDao().insert(product)
    suspend fun getAllProducts() = database.productDao().getAll()
    suspend fun getProductById(id: Long) = database.productDao().getById(id)

    // Cart operations
    suspend fun insertCartItem(cartItem: CartItem) = database.cartDao().insert(cartItem)
    suspend fun getCartByUserId(userId: Long) = database.cartDao().getCartByUserId(userId)
    suspend fun getCartItemCount(userId: Long) = database.cartDao().getCartItemCount(userId)
    suspend fun removeFromCart(userId: Long, productId: Long) =
        database.cartDao().removeFromCart(userId, productId)

    // Order operations
    suspend fun insertOrder(order: Order) = database.orderDao().insert(order)
    suspend fun getOrdersByUserId(userId: Long) = database.orderDao().getOrdersByUserId(userId)

    // Order Item operations
    suspend fun insertOrderItem(orderItem: OrderItem) = database.orderItemDao().insert(orderItem)

    // Store Location operations
    suspend fun insertStoreLocation(location: StoreLocation) = database.storeLocationDao().insert(location)
    suspend fun getAllStoreLocations() = database.storeLocationDao().getAllLocations()
}