package com.example.project_prm392.model

import androidx.room.*
import java.util.Date

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val product_id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "stock_quantity") val stockQuantity: Int,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "category") val category: String?,
    @ColumnInfo(name = "created_at") val createdAt: Date = Date()
)

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product)

    @Query("SELECT * FROM products")
    suspend fun getAll(): List<Product>

    @Query("SELECT * FROM products WHERE product_id = :productId")
    suspend fun getById(productId: Long): Product?

    @Query("DELETE FROM products WHERE product_id = :productId")
    suspend fun deleteById(productId: Long)
    @Update
    suspend fun update(product: Product) // ← This is the update function
}