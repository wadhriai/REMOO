package com.remoo.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remoo.app.data.models.Device
import com.remoo.app.data.models.DeviceType
import com.remoo.app.data.models.IrDatabase
import com.remoo.app.data.repository.DeviceRepository
import com.remoo.app.ir.IrScanner
import com.remoo.app.ir.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddDeviceStep {
    object ChooseType : AddDeviceStep()
    object ChooseBrand : AddDeviceStep()
    object Scanning : AddDeviceStep()
    object NameDevice : AddDeviceStep()
    object Done : AddDeviceStep()
}

@HiltViewModel
class AddDeviceViewModel @Inject constructor(
    private val repository: DeviceRepository,
    private val scanner: IrScanner
) : ViewModel() {

    var step by mutableStateOf<AddDeviceStep>(AddDeviceStep.ChooseType)
        private set

    var selectedType by mutableStateOf<DeviceType?>(null)
        private set

    var selectedBrand by mutableStateOf<String?>(null)
        private set

    var deviceName by mutableStateOf("")

    var scanProgress by mutableStateOf(0f)
        private set

    var scanStatus by mutableStateOf("")
        private set

    var scanResults by mutableStateOf<List<ScanResult>>(emptyList())
        private set

    var selectedScanResult by mutableStateOf<ScanResult?>(null)
        private set

    var isSaving by mutableStateOf(false)
        private set

    fun selectType(type: DeviceType) {
        selectedType = type
        deviceName = type.displayName
        step = AddDeviceStep.ChooseBrand
    }

    fun selectBrand(brand: String) {
        selectedBrand = brand
        step = AddDeviceStep.Scanning
        startScan()
    }

    fun skipScan() {
        step = AddDeviceStep.NameDevice
    }

    private fun startScan() {
        val type = selectedType ?: return
        viewModelScope.launch {
            scanResults = emptyList()
            scanner.scanDevice(type) { progress, status ->
                scanProgress = progress
                scanStatus = status
            }.also { results ->
                scanResults = results
                selectedScanResult = results.firstOrNull()
                step = AddDeviceStep.NameDevice
            }
        }
    }

    fun selectScanResult(result: ScanResult) {
        selectedScanResult = result
        selectedBrand = result.brand
    }

    fun saveDevice(onDone: () -> Unit) {
        val type = selectedType ?: return
        val brand = selectedBrand ?: "Unknown"
        isSaving = true
        viewModelScope.launch {
            val colorHex = when (type) {
                DeviceType.TV -> "#4FC3F7"
                DeviceType.AC -> "#81C784"
                DeviceType.ANDROID_BOX -> "#FFB74D"
                DeviceType.FAN -> "#80DEEA"
                DeviceType.PROJECTOR -> "#CE93D8"
                DeviceType.SOUNDBAR -> "#F48FB1"
                else -> "#BCAAFA"
            }
            val device = Device(
                name = deviceName.ifBlank { type.displayName },
                brand = brand,
                type = type,
                iconEmoji = type.emoji,
                colorHex = colorHex
            )
            repository.saveDevice(device)
            isSaving = false
            step = AddDeviceStep.Done
            onDone()
        }
    }

    fun back() {
        step = when (step) {
            is AddDeviceStep.ChooseBrand -> AddDeviceStep.ChooseType
            is AddDeviceStep.Scanning -> AddDeviceStep.ChooseBrand
            is AddDeviceStep.NameDevice -> AddDeviceStep.ChooseBrand
            else -> AddDeviceStep.ChooseType
        }
    }

    fun getBrandsForCurrentType(): List<String> =
        selectedType?.let { IrDatabase.getBrandsForType(it).map { b -> b.name } } ?: emptyList()
}
