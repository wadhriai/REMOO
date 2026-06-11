package com.remoo.app.ir

import com.remoo.app.data.models.DeviceType
import com.remoo.app.data.models.IrDatabase
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

data class ScanResult(
    val brand: String,
    val deviceType: DeviceType,
    val successRate: Float,
    val workingKeys: List<String>
)

@Singleton
class IrScanner @Inject constructor(private val irManager: IrManager) {

    suspend fun scanDevice(
        deviceType: DeviceType,
        onProgress: (Float, String) -> Unit
    ): List<ScanResult> {
        val brands = IrDatabase.getBrandsForType(deviceType)
        val results = mutableListOf<ScanResult>()
        val total = brands.size.toFloat()

        brands.forEachIndexed { index, brand ->
            onProgress((index / total), "Testing ${brand.name}...")
            delay(300)

            val workingKeys = mutableListOf<String>()
            brand.codes.entries.take(3).forEach { (key, pattern) ->
                val transmitted = irManager.transmit(irManager.NEC_FREQUENCY, pattern)
                if (transmitted) workingKeys.add(key)
                delay(150)
            }

            val successRate = if (brand.codes.isNotEmpty())
                workingKeys.size.toFloat() / minOf(brand.codes.size, 3).toFloat()
            else 0f

            if (successRate > 0 || !irManager.isAvailable) {
                results.add(ScanResult(brand.name, deviceType, successRate, workingKeys))
            }
        }
        onProgress(1f, "Scan complete")
        return results.sortedByDescending { it.successRate }
    }
}
