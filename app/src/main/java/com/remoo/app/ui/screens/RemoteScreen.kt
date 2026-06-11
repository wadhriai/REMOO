package com.remoo.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.remoo.app.data.models.DeviceType
import com.remoo.app.ui.components.GlassCard
import com.remoo.app.ui.components.RemoteButton
import com.remoo.app.ui.theme.*
import com.remoo.app.viewmodel.RemoteKey
import com.remoo.app.viewmodel.RemoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteScreen(
    onBack: () -> Unit,
    viewModel: RemoteViewModel = hiltViewModel()
) {
    val device = viewModel.device
    val keys = viewModel.getRemoteKeys()

    val deviceColor = device?.let {
        try { Color(android.graphics.Color.parseColor(it.colorHex)) }
        catch (e: Exception) { Accent }
    } ?: Accent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top color glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(deviceColor.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = OnSurface)
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (device != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(deviceColor.copy(alpha = 0.18f))
                                .border(1.dp, deviceColor.copy(alpha = 0.3f), RoundedCornerShape(11.dp))
                        ) {
                            Text(device.iconEmoji, fontSize = 20.sp)
                        }
                        Column {
                            Text(
                                device.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = OnBackground
                            )
                            Text(
                                "${device.brand} · ${device.type.displayName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceDim
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // IR transmit indicator
                AnimatedVisibility(
                    visible = viewModel.isTransmitting,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Accent)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Remote layout
            if (device != null) {
                when (device.type) {
                    DeviceType.TV -> TvRemoteLayout(keys, viewModel)
                    DeviceType.AC -> AcRemoteLayout(keys, viewModel)
                    else -> GenericRemoteLayout(keys, viewModel)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }
            }
        }
    }
}

@Composable
private fun TvRemoteLayout(keys: List<RemoteKey>, viewModel: RemoteViewModel) {
    val keyMap = keys.associateBy { it.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Power row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            keyMap["mute"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
            keyMap["power"]?.let {
                RemoteButton(
                    it.icon, it.label, { viewModel.pressKey(it.id) },
                    size = 68.dp, isHighlighted = true
                )
            }
            keyMap["source"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vol / Ch row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                keyMap["vol_up"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("VOL", style = MaterialTheme.typography.labelSmall, color = OnSurfaceDim)
                Spacer(modifier = Modifier.height(8.dp))
                keyMap["vol_down"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
            }

            // D-pad
            DPadCluster(keyMap, viewModel)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                keyMap["ch_up"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
                Spacer(modifier = Modifier.height(8.dp))
                Text("CH", style = MaterialTheme.typography.labelSmall, color = OnSurfaceDim)
                Spacer(modifier = Modifier.height(8.dp))
                keyMap["ch_down"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            keyMap["back"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
            keyMap["home"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
            keyMap["menu"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Divider, modifier = Modifier.padding(horizontal = 8.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // Numpad
        NumberPad(keyMap, viewModel)
    }
}

@Composable
private fun DPadCluster(keyMap: Map<String, RemoteKey>, viewModel: RemoteViewModel) {
    GlassCard(cornerRadius = 60.dp) {
        Box(
            modifier = Modifier.size(164.dp),
            contentAlignment = Alignment.Center
        ) {
            // Up
            Box(modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)) {
                keyMap["up"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }, size = 48.dp) }
            }
            // Left
            Box(modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)) {
                keyMap["left"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }, size = 48.dp) }
            }
            // OK
            keyMap["ok"]?.let {
                RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }, size = 52.dp, isHighlighted = true)
            }
            // Right
            Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)) {
                keyMap["right"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }, size = 48.dp) }
            }
            // Down
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)) {
                keyMap["down"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }, size = 48.dp) }
            }
        }
    }
}

@Composable
private fun NumberPad(keyMap: Map<String, RemoteKey>, viewModel: RemoteViewModel) {
    val nums = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        userScrollEnabled = false
    ) {
        items(nums) { num ->
            keyMap[num]?.let { key ->
                RemoteButton(
                    key.icon,
                    key.label,
                    { viewModel.pressKey(key.id) },
                    modifier = Modifier.fillMaxWidth(),
                    isWide = true
                )
            }
        }
    }
}

@Composable
private fun AcRemoteLayout(keys: List<RemoteKey>, viewModel: RemoteViewModel) {
    val keyMap = keys.associateBy { it.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Temperature display
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            tintColor = AcColor
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        keyMap["temp_down"]?.let {
                            RemoteButton(
                                it.icon, it.label, { viewModel.pressKey(it.id) },
                                size = 56.dp, isHighlighted = true
                            )
                        }
                        Text("❄️", fontSize = 48.sp)
                        keyMap["temp_up"]?.let {
                            RemoteButton(
                                it.icon, it.label, { viewModel.pressKey(it.id) },
                                size = 56.dp, isHighlighted = true
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            keyMap["power"]?.let {
                RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }, size = 64.dp, isHighlighted = true)
            }
            keyMap["mode"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
            keyMap["fan"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            keyMap["swing"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
            keyMap["sleep"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
            keyMap["timer"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
            keyMap["eco"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
            keyMap["turbo"]?.let { RemoteButton(it.icon, it.label, { viewModel.pressKey(it.id) }) }
        }
    }
}

@Composable
private fun GenericRemoteLayout(keys: List<RemoteKey>, viewModel: RemoteViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(keys) { key ->
            RemoteButton(
                key.icon,
                key.label,
                { viewModel.pressKey(key.id) },
                modifier = Modifier.fillMaxWidth(),
                isHighlighted = key.isHighlighted
            )
        }
    }
}
