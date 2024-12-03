package com.xdev.fastslip.screens.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    backgroundColor: Color = Color.White.copy(alpha = 0.7f),
    iconTint: Color = Color.Black,
    size: Dp = 40.dp,
    iconSize: Dp = 24.dp,
    shape: Shape = CircleShape,
    rotation: Float = 0f // Rotation angle in degrees
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size) // Overall button size
            .clip(shape) // Circular shape
            .background(color = backgroundColor) // Background color with alpha
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Go Back",
            tint = iconTint, // Icon color
            modifier = Modifier
                .size(iconSize) // Icon size within the button
                .rotate(rotation) // Apply rotation
        )
    }
}