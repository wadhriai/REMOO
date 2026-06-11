package com.remoo.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.remoo.app.ui.theme.Accent
import com.remoo.app.ui.theme.AccentGlow
import com.remoo.app.ui.theme.Divider
import com.remoo.app.ui.theme.OnSurface
import com.remoo.app.ui.theme.Surface2
import com.remoo.app.ui.theme.Surface3

@Composable
fun RemoteButton(
    icon: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    isHighlighted: Boolean = false,
    isPressed: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressing by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (pressing) 0.88f else 1f,
        animationSpec = tween(80),
        label = "btn_scale"
    )

    val bgColor = if (isHighlighted) {
        Brush.radialGradient(colors = listOf(Accent.copy(alpha = 0.35f), AccentGlow))
    } else {
        Brush.verticalGradient(colors = listOf(Surface3, Surface2))
    }

    val borderColor = if (isHighlighted) Accent.copy(alpha = 0.5f) else Divider

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.scale(scale)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
        ) {
            Text(
                text = icon,
                fontSize = if (icon.length <= 2) 20.sp else 14.sp,
                color = if (isHighlighted) Color.White else OnSurface,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isHighlighted) Accent else OnSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
