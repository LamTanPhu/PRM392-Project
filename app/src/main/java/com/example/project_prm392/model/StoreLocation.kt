package com.example.project_prm392.model

import androidx.room.*

@Entity(tableName = "store_locations") // Removed unnecessary index on primary key
data class StoreLocation(
    @PrimaryKey(autoGenerate = true) val location_id: Long = 0,
    @ColumnInfo(name = "store_name") val storeName: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "phone") val phone: String?,
    @ColumnInfo(name = "opening_hours") val openingHours: String?
)

@Dao
interface StoreLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: StoreLocation)

    @Query("SELECT * FROM store_locations")
    suspend fun getAllLocations(): List<StoreLocation> // For map screen

}