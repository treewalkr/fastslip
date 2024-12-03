package com.xdev.fastslip.screens.camera.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xdev.fastslip.utils.getScreenDimensions
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun QrCodeBox() {
    val screenDim = getScreenDimensions()
    val hInDp = screenDim.height
    val wInDp = screenDim.width
    val yOffset = (hInDp - wInDp) * 1.1f

    Log.d("QrCodeBox", "Setting up QrCodeBox")

    val context = LocalContext.current
    val imageFileName = "saved_qr_code.png"

    // State to hold the selected image
    var selectedImage by remember { mutableStateOf(loadImageFromStorage(context, imageFileName)) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                // Load and save the image
                val bitmap = loadBitmapFromUri(context, uri)
                bitmap?.let {
                    saveImageToStorage(context, it, imageFileName)
                    selectedImage = it.asImageBitmap()
                }
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = yOffset),
        contentAlignment = Alignment.Center
    ) {
        // Display the selected image, or a button if no image is selected
        if (selectedImage != null) {
            Image(
                bitmap = selectedImage!!,
                contentDescription = "Selected image",
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                Log.d("QrCodeBox", "Image long-pressed, opening image picker")
                                imagePickerLauncher.launch("image/*")
                            }
                        )
                    },
                contentScale = ContentScale.Fit
            )
        } else {
            Button(
                onClick = {
                    Log.d("QrCodeBox", "Image picker button clicked")
                    imagePickerLauncher.launch("image/*")
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .offset(y = (hInDp - yOffset) / 2)
            ) {
                Text("Select an Image")
            }
        }
    }
}

fun loadBitmapFromUri(context: Context, uri: android.net.Uri): Bitmap? {
    return context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it)
    }
}

fun saveImageToStorage(context: Context, bitmap: Bitmap, fileName: String) {
    val file = File(context.filesDir, fileName)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        Log.d("QrCodeBox", "Image saved to storage: ${file.absolutePath}")
    }
}

fun loadImageFromStorage(context: Context, fileName: String): ImageBitmap? {
    val file = File(context.filesDir, fileName)
    return if (file.exists()) {
        BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
    } else {
        null
    }
}