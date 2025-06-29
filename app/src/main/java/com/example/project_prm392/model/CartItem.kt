package com.example.project_prm392.model

import androidx.room.*
import java.util.Date

@Entity(
    tableName = "cart",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["product_id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["product_id"]),
        Index(value = ["user_id", "product_id"], unique = true) // Composite index to prevent duplicate cart items
    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val cart_id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "product_id") val productId: Long,
    @ColumnInfo(name = "quantity") val quantity: Int = 1,
    @ColumnInfo(name = "added_at") val addedAt: Date = Date()
)

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItem: CartItem)

    @Query("SELECT * FROM cart WHERE user_id = :userId")
    suspend fun getCartByUserId(userId: Long): List<CartItem>

    @Query("SELECT COUNT(*) FROM cart WHERE user_id = :userId")
    suspend fun getCartItemCount(userId: Long): Int

    // Additional useful queries
    @Query("DELETE FROM cart WHERE user_id = :userId AND product_id = :productId")
    suspend fun removeFromCart(userId: Long, productId: Long)

    @Query("DELETE FROM cart WHERE user_id = :userId")
    suspend fun clearCart(userId: Long)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)
}