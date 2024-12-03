package com.xdev.fastslip.screens.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import fast_slip.composeapp.generated.resources.Res
import fast_slip.composeapp.generated.resources.check
import fast_slip.composeapp.generated.resources.error
import fast_slip.composeapp.generated.resources.content_copy
import fast_slip.composeapp.generated.resources.open_in_new

@Composable
fun QRCodeScanResultDialog(
    scanResult: String,
    isSuccess: Boolean,
    onDismissRequest: () -> Unit,
    onScanAnother: () -> Unit,
    onAutoClose: () -> Unit
) {
    var showIcon by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableStateOf(10) }
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(500)
        showIcon = true

        // Countdown timer
        for (i in 10 downTo 1) {
            remainingSeconds = i
            delay(1000)
        }
        onAutoClose()
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(if (isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(if (isSuccess) Res.drawable.check else Res.drawable.error),
                        contentDescription = if (isSuccess) "Success" else "Error",
                        tint = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Result Message
                Text(
                    if (isSuccess) "QR Code Scanned!" else "Scan Failed",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Text(
                    if (isSuccess) "Here's what we found:" else "An error occurred during scanning:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Scanned Result or Error Message
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        scanResult,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Action Buttons
                if (isSuccess) {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(scanResult))
                            coroutineScope.launch {
                                copied = true
                                delay(2000)
                                copied = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (copied) "Copied!" else "Copy to Clipboard")
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(Res.drawable.content_copy),
                            contentDescription = "Copy"
                        )
                    }

                    if (scanResult.startsWith("http", ignoreCase = true)) {
                        Button(
                            onClick = { uriHandler.openUri(scanResult) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text("Open Link")
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(Res.drawable.open_in_new),
                                contentDescription = "Open Link"
                            )
                        }
                    }
                }

                // Scan Another Button
                OutlinedButton(
                    onClick = onScanAnother,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Scan Another Code")
                }

                // Countdown timer
                Text(
                    "This dialog will close in $remainingSeconds seconds",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}