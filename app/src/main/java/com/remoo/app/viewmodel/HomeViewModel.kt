package com.remoo.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.remoo.app.data.models.Device
import com.remoo.app.data.repository.DeviceRepository
import com.remoo.app.ir.IrManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DeviceRepository,
    val irManager: IrManager
) : ViewModel() {

    val devices: StateFlow<List<Device>> = repository.getAllDevices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteDevice(device: Device) {
        viewModelScope.launch { repository.deleteDevice(device) }
    }

    fun markUsed(id: Long) {
        viewModelScope.launch { repository.markUsed(id) }
    }
}
