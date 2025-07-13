package com.example.project_prm392.DAO

import com.example.project_prm392.Database.ElectronicStoreDatabase
import com.example.project_prm392.model.*

class AppRepository(private val database: ElectronicStoreDatabase) {

    // User operations
    suspend fun insertUser(user: User) = database.userDao().insert(user)
    suspend fun getUserByUsername(username: String) = database.userDao().getByUsername(username)
    suspend fun  getUserByUserId(userId:Long)=database.userDao().getByUserId(userId)
    suspend fun  getAllUser()=database.userDao().getAllUser()
    suspend fun  updateUser(user:User)=database.userDao().update(user)
    suspend fun deleteUserById(userId: Long) {
        database.userDao().deleteById(userId)
    }

    // Product operations
    suspend fun insertProduct(product: Product) = database.productDao().insert(product)
    suspend fun getAllProducts() = database.productDao().getAll()
    suspend fun deleteProductById(productId: Long) {
        database.productDao().deleteById(productId)
    }

    suspend fun getProductById(id: Long) = database.productDao().getById(id)
    suspend fun  updateProduct(product:Product)=database.productDao().update(product)
    // Cart operations
    suspend fun insertCartItem(cartItem: CartItem) = database.cartDao().insert(cartItem)
    suspend fun getCartByUserId(userId: Long) = database.cartDao().getCartByUserId(userId)
    suspend fun getCartItemCount(userId: Long) = database.cartDao().getCartItemCount(userId)
    suspend fun removeFromCart(userId: Long, productId: Long) =
        database.cartDao().removeFromCart(userId, productId)
    suspend fun getCartItem(userId: Long, productId: Long): CartItem? {
        return database.cartDao().getCartItem(userId, productId)
    }

    suspend fun updateCartItem(cartItem: CartItem) {
        database.cartDao().updateCartItem(cartItem)
    }


    // Order operations
    suspend fun updateOrderStatus(orderId: Long, status: String) {
        database.orderDao().updateOrderStatus(orderId, status)
    }

    suspend fun insertOrder(order: Order) = database.orderDao().insert(order)
    suspend fun getOrdersByUserId(userId: Long) = database.orderDao().getOrdersByUserId(userId)
    suspend fun getLatestOrderByUser(userId: Long): Order =
        database.orderDao().getLatestOrderByUser(userId)
    suspend fun getUserIdByOrderId(orderId: Long): Long? {
        return database.orderDao().getUserIdByOrderId(orderId)
    }


    // Order Item operations
    suspend fun insertOrderItem(orderItem: OrderItem) = database.orderItemDao().insert(orderItem)
    suspend fun getOrderItemsByOrderId(orderId: Long): List<OrderItem> {
        return database.orderItemDao().getOrderItemsByOrderId(orderId)
    }

    // Store Location operations
    suspend fun insertStoreLocation(location: StoreLocation) = database.storeLocationDao().insert(location)
    suspend fun getAllStoreLocations() = database.storeLocationDao().getAllLocations()
}