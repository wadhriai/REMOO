package com.remoo.app.data.repository

import androidx.room.*
import com.remoo.app.data.models.Device
import com.remoo.app.data.models.DeviceType
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY lastUsed DESC")
    fun getAllDevices(): Flow<List<Device>>

    @Query("SELECT * FROM devices WHERE type = :type ORDER BY lastUsed DESC")
    fun getDevicesByType(type: DeviceType): Flow<List<Device>>

    @Query("SELECT * FROM devices WHERE id = :id")
    suspend fun getDeviceById(id: Long): Device?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: Device): Long

    @Update
    suspend fun updateDevice(device: Device)

    @Delete
    suspend fun deleteDevice(device: Device)

    @Query("UPDATE devices SET lastUsed = :timestamp WHERE id = :id")
    suspend fun updateLastUsed(id: Long, timestamp: Long)
}
