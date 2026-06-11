package com.remoo.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.remoo.app.data.models.Device
import com.remoo.app.data.models.DeviceType
import com.remoo.app.ui.components.GlassCard
import com.remoo.app.ui.theme.*
import com.remoo.app.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddDevice: () -> Unit,
    onOpenRemote: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val devices by viewModel.devices.collectAsState()
    var filterType by remember { mutableStateOf<DeviceType?>(null) }
    var deviceToDelete by remember { mutableStateOf<Device?>(null) }

    val filteredDevices = if (filterType == null) devices
    else devices.filter { it.type == filterType }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Background ambient glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-60).dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Accent.copy(alpha = 0.12f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(56.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "remoo",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-1).sp
                            ),
                            color = OnBackground
                        )
                        Text(
                            text = "Your IR remote control hub",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceDim
                        )
                    }
                    if (!viewModel.irManager.isAvailable) {
                        GlassCard(cornerRadius = 12.dp) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.WifiTethering,
                                    contentDescription = null,
                                    tint = Warning,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text("No IR", style = MaterialTheme.typography.labelSmall, color = Warning)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filterType == null,
                            onClick = { filterType = null },
                            label = { Text("All") }
                        )
                    }
                    items(DeviceType.values()) { type ->
                        FilterChip(
                            selected = filterType == type,
                            onClick = { filterType = if (filterType == type) null else type },
                            label = { Text("${type.emoji} ${type.displayName.split(" ").first()}") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (filteredDevices.isEmpty()) {
                item {
                    EmptyState(onAddDevice = onAddDevice)
                }
            } else {
                items(filteredDevices) { device ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                    ) {
                        DeviceCard(
                            device = device,
                            onClick = {
                                viewModel.markUsed(device.id)
                                onOpenRemote(device.id)
                            },
                            onDelete = { deviceToDelete = device },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onAddDevice,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Accent,
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Add device", modifier = Modifier.size(28.dp))
        }
    }

    deviceToDelete?.let { device ->
        AlertDialog(
            onDismissRequest = { deviceToDelete = null },
            title = { Text("Remove device?") },
            text = { Text("\"${device.name}\" will be removed from your remotes.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDevice(device)
                        deviceToDelete = null
                    }
                ) { Text("Remove", color = Error) }
            },
            dismissButton = {
                TextButton(onClick = { deviceToDelete = null }) { Text("Cancel") }
            },
            containerColor = Surface2
        )
    }
}

@Composable
private fun DeviceCard(
    device: Device,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = try {
        Color(android.graphics.Color.parseColor(device.colorHex))
    } catch (e: Exception) { Accent }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        tintColor = accentColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.18f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Text(device.iconEmoji, fontSize = 24.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnBackground
                )
                Text(
                    text = "${device.brand} · ${device.type.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceDim
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = OnSurfaceDim.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(onAddDevice: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📡", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "No devices yet",
            style = MaterialTheme.typography.headlineMedium,
            color = OnBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Tap + to add your TV, AC,\nAndroid Box, or any IR device",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceDim,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onAddDevice,
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Device", style = MaterialTheme.typography.titleMedium)
        }
    }
}
