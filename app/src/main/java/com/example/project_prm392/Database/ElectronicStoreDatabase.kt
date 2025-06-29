package com.example.project_prm392.Database

import androidx.room.*
import androidx.room.RoomDatabase
import com.example.project_prm392.model.CartDao
import com.example.project_prm392.model.CartItem
import com.example.project_prm392.model.Order
import com.example.project_prm392.model.OrderDao
import com.example.project_prm392.model.OrderItem
import com.example.project_prm392.model.OrderItemDao
import com.example.project_prm392.model.Product
import com.example.project_prm392.model.ProductDao
import com.example.project_prm392.model.StoreLocation
import com.example.project_prm392.model.StoreLocationDao
import com.example.project_prm392.model.User
import com.example.project_prm392.model.UserDao

@Database(
    entities = [
        User::class,
        Product::class,
        CartItem::class,
        Order::class,
        OrderItem::class,
        StoreLocation::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class) // Add this line
abstract class ElectronicStoreDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun storeLocationDao(): StoreLocationDao
}