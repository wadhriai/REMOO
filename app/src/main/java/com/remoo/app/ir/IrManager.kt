package com.remoo.app.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IrManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val irManager: ConsumerIrManager? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
        } else null
    }

    val isAvailable: Boolean get() = irManager?.hasIrEmitter() == true

    fun transmit(frequency: Int, pattern: IntArray): Boolean {
        if (!isAvailable) return false
        return try {
            irManager?.transmit(frequency, pattern)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun transmit(frequency: Int, pattern: List<Int>): Boolean =
        transmit(frequency, pattern.toIntArray())

    // NEC protocol frequency: 38kHz
    val NEC_FREQUENCY = 38000

    // RC5 protocol frequency: 36kHz
    val RC5_FREQUENCY = 36000

    // Samsung protocol frequency: 38kHz
    val SAMSUNG_FREQUENCY = 38000

    // Sony SIRC frequency: 40kHz
    val SONY_FREQUENCY = 40000
}
