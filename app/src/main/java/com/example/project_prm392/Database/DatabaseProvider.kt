package com.example.project_prm392.Database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.project_prm392.model.Product
import com.example.project_prm392.model.StoreLocation
import com.example.project_prm392.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

object DatabaseProvider {
    @Volatile
    private var INSTANCE: ElectronicStoreDatabase? = null

    fun getDatabase(context: Context): ElectronicStoreDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                ElectronicStoreDatabase::class.java,
                "electronic_store_db"
            )
                .fallbackToDestructiveMigration() // Add this for development
                .addCallback(DatabaseCallback())
                .build()
            INSTANCE = instance
            instance
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Use the INSTANCE instead of calling getDatabase again to avoid recursion
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        populateDatabase(database)
                    } catch (e: Exception) {
                        e.printStackTrace() // Log error instead of crashing
                    }
                }
            }
        }

        private suspend fun populateDatabase(database: ElectronicStoreDatabase) {
            // Sample Users
            database.userDao().insert(
                User(
                    username = "testuser",
                    email = "test@example.com",
                    passwordHash = "password123",
                    fullName = "Test User",
                    phoneNumber = "1234567890"
                )
            )

            // Sample Products
            database.productDao().insert(
                Product(
                    name = "Smartphone",
                    description = "Latest model with 128GB storage",
                    price = 599.99,
                    stockQuantity = 50,
                    imageUrl = "https://example.com/smartphone.jpg",
                    category = "Electronics"
                )
            )

            database.productDao().insert(
                Product(
                    name = "Laptop",
                    description = "High-performance laptop",
                    price = 999.99,
                    stockQuantity = 30,
                    imageUrl = "https://example.com/laptop.jpg",
                    category = "Electronics"
                ),

            )
            database.productDao().insert(
                Product(
                    name = "Chuột máy tính",
                    description = "Chuột không dây Logitech",
                    price = 250_000.0,
                    stockQuantity = 100,
                    imageUrl = "https://example.com/mouse.jpg",
                    category = "Phụ kiện"
                ),

                )


            // Sample Store Location
            database.storeLocationDao().insert(
                StoreLocation(
                    storeName = "Main Store",
                    address = "Ho Chi Minh City",
                    latitude = 10.7769,
                    longitude = 106.7009,
                    phone = "0123456789",
                    openingHours = "9AM - 9PM"
                )
            )
        }
    }
}