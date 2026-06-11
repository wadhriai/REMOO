package com.remoo.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remoo.app.data.models.Device
import com.remoo.app.data.models.DeviceType
import com.remoo.app.data.models.IrDatabase
import com.remoo.app.data.repository.DeviceRepository
import com.remoo.app.ir.IrManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RemoteKey(
    val id: String,
    val label: String,
    val icon: String,
    val isHighlighted: Boolean = false,
    val isWide: Boolean = false
)

@HiltViewModel
class RemoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DeviceRepository,
    val irManager: IrManager
) : ViewModel() {

    private val deviceId: Long = savedStateHandle.get<Long>("deviceId") ?: 0L

    var device by mutableStateOf<Device?>(null)
        private set

    var lastPressedKey by mutableStateOf<String?>(null)
        private set

    var isTransmitting by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            device = repository.getDeviceById(deviceId)
            repository.markUsed(deviceId)
        }
    }

    fun pressKey(keyId: String) {
        val dev = device ?: return
        viewModelScope.launch {
            isTransmitting = true
            lastPressedKey = keyId

            val brandEntry = IrDatabase.getBrandsForType(dev.type)
                .find { it.name == dev.brand }
                ?: IrDatabase.getBrandsForType(dev.type).lastOrNull()

            val pattern = brandEntry?.codes?.get(keyId)
            if (pattern != null) {
                irManager.transmit(irManager.NEC_FREQUENCY, pattern)
            }

            kotlinx.coroutines.delay(100)
            isTransmitting = false
        }
    }

    fun getRemoteKeys(): List<RemoteKey> = when (device?.type) {
        DeviceType.TV -> tvKeys()
        DeviceType.AC -> acKeys()
        DeviceType.ANDROID_BOX -> boxKeys()
        DeviceType.FAN -> fanKeys()
        DeviceType.PROJECTOR -> projectorKeys()
        DeviceType.SOUNDBAR -> soundbarKeys()
        else -> genericKeys()
    }

    private fun tvKeys() = listOf(
        RemoteKey("power", "Power", "⏻", isHighlighted = true),
        RemoteKey("mute", "Mute", "🔇"),
        RemoteKey("source", "Source", "⬛"),
        RemoteKey("menu", "Menu", "☰"),
        RemoteKey("vol_up", "Vol +", "🔊"),
        RemoteKey("ch_up", "CH +", "⬆"),
        RemoteKey("vol_down", "Vol -", "🔉"),
        RemoteKey("ch_down", "CH -", "⬇"),
        RemoteKey("up", "▲", "▲"),
        RemoteKey("left", "◄", "◄"),
        RemoteKey("ok", "OK", "●", isHighlighted = true),
        RemoteKey("right", "►", "►"),
        RemoteKey("down", "▼", "▼"),
        RemoteKey("back", "Back", "↩"),
        RemoteKey("home", "Home", "⌂"),
        RemoteKey("1", "1", "1"),
        RemoteKey("2", "2", "2"),
        RemoteKey("3", "3", "3"),
        RemoteKey("4", "4", "4"),
        RemoteKey("5", "5", "5"),
        RemoteKey("6", "6", "6"),
        RemoteKey("7", "7", "7"),
        RemoteKey("8", "8", "8"),
        RemoteKey("9", "9", "9"),
        RemoteKey("0", "0", "0", isWide = true)
    )

    private fun acKeys() = listOf(
        RemoteKey("power", "Power", "⏻", isHighlighted = true),
        RemoteKey("mode", "Mode", "◈"),
        RemoteKey("temp_up", "Temp +", "🌡+", isHighlighted = true),
        RemoteKey("fan", "Fan", "🌀"),
        RemoteKey("temp_down", "Temp -", "🌡-", isHighlighted = true),
        RemoteKey("swing", "Swing", "↕"),
        RemoteKey("sleep", "Sleep", "😴"),
        RemoteKey("timer", "Timer", "⏱"),
        RemoteKey("eco", "Eco", "🌿"),
        RemoteKey("turbo", "Turbo", "⚡")
    )

    private fun boxKeys() = listOf(
        RemoteKey("power", "Power", "⏻", isHighlighted = true),
        RemoteKey("home", "Home", "⌂"),
        RemoteKey("back", "Back", "↩"),
        RemoteKey("menu", "Menu", "☰"),
        RemoteKey("up", "▲", "▲"),
        RemoteKey("left", "◄", "◄"),
        RemoteKey("ok", "OK", "●", isHighlighted = true),
        RemoteKey("right", "►", "►"),
        RemoteKey("down", "▼", "▼"),
        RemoteKey("vol_up", "Vol +", "🔊"),
        RemoteKey("vol_down", "Vol -", "🔉"),
        RemoteKey("mute", "Mute", "🔇"),
        RemoteKey("play_pause", "Play/Pause", "⏯", isHighlighted = true),
        RemoteKey("rewind", "Rewind", "⏪"),
        RemoteKey("fast_forward", "FF", "⏩"),
        RemoteKey("settings", "Settings", "⚙")
    )

    private fun fanKeys() = listOf(
        RemoteKey("power", "Power", "⏻", isHighlighted = true),
        RemoteKey("speed_up", "Speed +", "▲"),
        RemoteKey("speed_down", "Speed -", "▼"),
        RemoteKey("oscillate", "Oscillate", "↔"),
        RemoteKey("timer", "Timer", "⏱"),
        RemoteKey("sleep", "Sleep", "😴"),
        RemoteKey("natural", "Natural", "🌿"),
        RemoteKey("turbo", "Turbo", "⚡")
    )

    private fun projectorKeys() = listOf(
        RemoteKey("power", "Power", "⏻", isHighlighted = true),
        RemoteKey("source", "Source", "⬛"),
        RemoteKey("menu", "Menu", "☰"),
        RemoteKey("up", "▲", "▲"),
        RemoteKey("left", "◄", "◄"),
        RemoteKey("ok", "OK", "●", isHighlighted = true),
        RemoteKey("right", "►", "►"),
        RemoteKey("down", "▼", "▼"),
        RemoteKey("back", "Back", "↩"),
        RemoteKey("zoom_in", "Zoom +", "🔍+"),
        RemoteKey("zoom_out", "Zoom -", "🔍-"),
        RemoteKey("freeze", "Freeze", "❄"),
        RemoteKey("blank", "Blank", "⬛"),
        RemoteKey("vol_up", "Vol +", "🔊"),
        RemoteKey("vol_down", "Vol -", "🔉"),
        RemoteKey("mute", "Mute", "🔇")
    )

    private fun soundbarKeys() = listOf(
        RemoteKey("power", "Power", "⏻", isHighlighted = true),
        RemoteKey("vol_up", "Vol +", "🔊", isHighlighted = true),
        RemoteKey("vol_down", "Vol -", "🔉", isHighlighted = true),
        RemoteKey("mute", "Mute", "🔇"),
        RemoteKey("source", "Source", "⬛"),
        RemoteKey("eq", "EQ", "🎵"),
        RemoteKey("bass_up", "Bass +", "↑"),
        RemoteKey("bass_down", "Bass -", "↓"),
        RemoteKey("bluetooth", "BT", "🔵"),
        RemoteKey("play_pause", "Play/Pause", "⏯")
    )

    private fun genericKeys() = listOf(
        RemoteKey("power", "Power", "⏻", isHighlighted = true),
        RemoteKey("vol_up", "Vol +", "🔊"),
        RemoteKey("vol_down", "Vol -", "🔉"),
        RemoteKey("mute", "Mute", "🔇"),
        RemoteKey("ok", "OK", "●", isHighlighted = true),
        RemoteKey("back", "Back", "↩"),
        RemoteKey("menu", "Menu", "☰"),
        RemoteKey("up", "▲", "▲"),
        RemoteKey("left", "◄", "◄"),
        RemoteKey("right", "►", "►"),
        RemoteKey("down", "▼", "▼")
    )
}
