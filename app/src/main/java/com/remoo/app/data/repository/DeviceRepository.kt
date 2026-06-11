package com.remoo.app.data.repository

import com.remoo.app.data.models.Device
import com.remoo.app.data.models.DeviceType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepository @Inject constructor(private val dao: DeviceDao) {

    fun getAllDevices(): Flow<List<Device>> = dao.getAllDevices()

    fun getDevicesByType(type: DeviceType): Flow<List<Device>> = dao.getDevicesByType(type)

    suspend fun getDeviceById(id: Long): Device? = dao.getDeviceById(id)

    suspend fun saveDevice(device: Device): Long = dao.insertDevice(device)

    suspend fun updateDevice(device: Device) = dao.updateDevice(device)

    suspend fun deleteDevice(device: Device) = dao.deleteDevice(device)

    suspend fun markUsed(id: Long) = dao.updateLastUsed(id, System.currentTimeMillis())
}
