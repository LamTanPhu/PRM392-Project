package com.example.project_prm392.model


import androidx.room.*
import java.util.Date

@Entity(tableName = "users", indices = [Index(value = ["username", "email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val user_id: Long = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "password_hash") val passwordHash: String,
    @ColumnInfo(name = "full_name") val fullName: String?,
    @ColumnInfo(name = "phone_number") val phoneNumber: String?,
    @ColumnInfo(name = "created_at") val createdAt: Date = Date()
)

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getByUsername(username: String): User?
}