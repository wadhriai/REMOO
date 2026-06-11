package com.remoo.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

enum class DeviceType(val displayName: String, val emoji: String) {
    TV("Television", "📺"),
    AC("Air Conditioner", "❄️"),
    ANDROID_BOX("Android Box", "📦"),
    FAN("Fan / Ceiling Fan", "🌀"),
    PROJECTOR("Projector", "📽️"),
    SOUNDBAR("Soundbar / Speaker", "🔊"),
    DVD("DVD / Blu-ray", "💿"),
    CABLE("Cable / Satellite", "📡"),
    OTHER("Other Device", "🔧")
}

@Entity(tableName = "devices")
@TypeConverters(StringListConverter::class)
data class Device(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val type: DeviceType,
    val iconEmoji: String = type.emoji,
    val colorHex: String = "#6C63FF",
    val customKeys: List<IrKey> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsed: Long = System.currentTimeMillis()
)

data class IrKey(
    val name: String,
    val icon: String,
    val frequency: Int,
    val pattern: List<Int>
)

class StringListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromIrKeyList(keys: List<IrKey>): String = gson.toJson(keys)

    @TypeConverter
    fun toIrKeyList(json: String): List<IrKey> {
        val type = object : TypeToken<List<IrKey>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    @TypeConverter
    fun fromDeviceType(type: DeviceType): String = type.name

    @TypeConverter
    fun toDeviceType(name: String): DeviceType = DeviceType.valueOf(name)
}
