package com.remoo.app.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.remoo.app.data.models.Device
import com.remoo.app.data.models.StringListConverter

@Database(entities = [Device::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverter::class)
abstract class DeviceDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}
