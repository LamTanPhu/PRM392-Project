package com.example.project_prm392.model

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class Order(
    @PrimaryKey(autoGenerate = true) val order_id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "address") val address: String, // Shipping address as text
    @ColumnInfo(name = "total_amount") val totalAmount: Double,
    @ColumnInfo(name = "status") val status: String, // e.g., Pending, Shipped, Delivered
    @ColumnInfo(name = "payment_method") val paymentMethod: String?,
    @ColumnInfo(name = "order_date") val orderDate: Date = Date()
)

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)

    @Query("SELECT * FROM orders WHERE user_id = :userId")
    suspend fun getOrdersByUserId(userId: Long): List<Order>
}