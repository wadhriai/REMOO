package com.remoo.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.remoo.app.data.models.DeviceType
import com.remoo.app.ui.components.GlassCard
import com.remoo.app.ui.theme.*
import com.remoo.app.viewmodel.AddDeviceStep
import com.remoo.app.viewmodel.AddDeviceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: AddDeviceViewModel = hiltViewModel()
) {
    if (viewModel.step is AddDeviceStep.Done) {
        LaunchedEffect(Unit) { onDone() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = 100.dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(colors = listOf(Accent.copy(alpha = 0.1f), Color.Transparent)),
                    CircleShape
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (viewModel.step is AddDeviceStep.ChooseType) onBack()
                        else viewModel.back()
                    }
                ) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = OnSurface)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (viewModel.step) {
                        is AddDeviceStep.ChooseType -> "Select Device Type"
                        is AddDeviceStep.ChooseBrand -> "Select Brand"
                        is AddDeviceStep.Scanning -> "Scanning..."
                        is AddDeviceStep.NameDevice -> "Name Your Device"
                        else -> "Add Device"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnBackground
                )
            }

            // Step progress
            StepProgress(
                current = when (viewModel.step) {
                    is AddDeviceStep.ChooseType -> 0
                    is AddDeviceStep.ChooseBrand -> 1
                    is AddDeviceStep.Scanning -> 2
                    is AddDeviceStep.NameDevice -> 3
                    else -> 3
                },
                total = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = viewModel.step,
                transitionSpec = {
                    slideInHorizontally { it / 3 } + fadeIn() togetherWith
                            slideOutHorizontally { -it / 3 } + fadeOut()
                },
                label = "step_anim"
            ) { step ->
                when (step) {
                    is AddDeviceStep.ChooseType -> ChooseTypeStep(viewModel)
                    is AddDeviceStep.ChooseBrand -> ChooseBrandStep(viewModel)
                    is AddDeviceStep.Scanning -> ScanningStep(viewModel)
                    is AddDeviceStep.NameDevice -> NameDeviceStep(viewModel)
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun StepProgress(current: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(total + 1) { i ->
            val filled = i <= current
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(
                        if (filled) Accent else Divider
                    )
            )
        }
    }
}

@Composable
private fun ChooseTypeStep(viewModel: AddDeviceViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(DeviceType.values()) { type ->
            val color = when (type) {
                DeviceType.TV -> TvColor
                DeviceType.AC -> AcColor
                DeviceType.ANDROID_BOX -> BoxColor
                DeviceType.FAN -> FanColor
                DeviceType.PROJECTOR -> ProjectorColor
                DeviceType.SOUNDBAR -> SoundbarColor
                else -> OtherColor
            }
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.4f)
                    .clickable { viewModel.selectType(type) },
                tintColor = color
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(type.emoji, fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = type.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = OnBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ChooseBrandStep(viewModel: AddDeviceViewModel) {
    val brands = viewModel.getBrandsForCurrentType()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Select the brand of your ${viewModel.selectedType?.displayName}",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceDim,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        brands.forEach { brand ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { viewModel.selectBrand(brand) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = brand,
                        style = MaterialTheme.typography.titleMedium,
                        color = OnBackground,
                        modifier = Modifier.weight(1f)
                    )
                    Text("›", color = OnSurfaceDim, fontSize = 20.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ScanningStep(viewModel: AddDeviceViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { viewModel.scanProgress },
                modifier = Modifier.size(120.dp),
                strokeWidth = 6.dp,
                color = Accent,
                trackColor = Surface2
            )
            Text(
                "${(viewModel.scanProgress * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                color = OnBackground,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            viewModel.scanStatus.ifBlank { "Searching for IR codes..." },
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceDim,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Point your phone at the device",
            style = MaterialTheme.typography.labelMedium,
            color = OnSurfaceDim.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NameDeviceStep(viewModel: AddDeviceViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        if (viewModel.scanResults.isNotEmpty()) {
            Text(
                "Scan Results",
                style = MaterialTheme.typography.titleMedium,
                color = OnBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            viewModel.scanResults.take(3).forEach { result ->
                val isSelected = result == viewModel.selectedScanResult
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.selectScanResult(result) },
                    tintColor = if (isSelected) Accent else Color.Transparent
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(result.brand, style = MaterialTheme.typography.titleMedium, color = OnBackground)
                            Text(
                                "${(result.successRate * 100).toInt()}% match",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (result.successRate > 0.5f) Success else OnSurfaceDim
                            )
                        }
                        if (isSelected) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = Accent)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        Text("Name your device", style = MaterialTheme.typography.titleMedium, color = OnBackground)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.deviceName,
            onValueChange = { viewModel.deviceName = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g. Living Room TV") },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent,
                unfocusedBorderColor = Divider,
                focusedTextColor = OnBackground,
                unfocusedTextColor = OnBackground,
                cursorColor = Accent,
                focusedContainerColor = Surface2,
                unfocusedContainerColor = Surface1
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { viewModel.saveDevice {} },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            enabled = !viewModel.isSaving,
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (viewModel.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Save Device", style = MaterialTheme.typography.titleMedium)
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}
