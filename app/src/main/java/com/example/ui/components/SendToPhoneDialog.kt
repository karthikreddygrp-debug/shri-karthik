package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.StoreConstants
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.NavyDeep
import com.example.util.QrCodeGenerator
import com.example.util.StoreIntents

@Composable
fun SendToPhoneDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var qrModeMaps by remember { mutableStateOf(true) }

    val qrPayload = remember(qrModeMaps) {
        if (qrModeMaps) {
            StoreConstants.GOOGLE_MAPS_VIEW_URL
        } else {
            "https://wa.me/${StoreConstants.WHATSAPP_NUMBER}"
        }
    }
    val qrMatrix = remember(qrPayload) { QrCodeGenerator.generate(qrPayload) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Send Store Details to Phone",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Scan this QR code with another phone camera or use quick-send actions below.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = qrModeMaps,
                        onClick = { qrModeMaps = true },
                        label = { Text("Store Map QR") }
                    )
                    FilterChip(
                        selected = !qrModeMaps,
                        onClick = { qrModeMaps = false },
                        label = { Text("WhatsApp QR") }
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CrispWhite,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier
                            .padding(14.dp)
                            .size(180.dp)
                            .background(CrispWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(168.dp)) {
                            val n = qrMatrix.size
                            val cellSize = size.width / n.toFloat()
                            for (y in 0 until n) {
                                for (x in 0 until n) {
                                    if (qrMatrix.isDark(x, y)) {
                                        drawRect(
                                            color = NavyDeep,
                                            topLeft = Offset(x * cellSize, y * cellSize),
                                            size = Size(cellSize + 0.5f, cellSize + 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { StoreIntents.shareStore(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("send_to_phone_share"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send via Messaging / Share")
                }

                OutlinedButton(
                    onClick = { StoreIntents.saveStoreContact(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("send_to_phone_contact")
                ) {
                    Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Store to Phone Contacts")
                }

                OutlinedButton(
                    onClick = {
                        StoreIntents.copyToClipboard(
                            context,
                            "Store Address & Phone",
                            "${StoreConstants.BUSINESS_NAME}\n${StoreConstants.ADDRESS}\nPlus Code: ${StoreConstants.PLUS_CODE}\nPhone: ${StoreConstants.PHONE_DISPLAY}"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("send_to_phone_copy")
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy Store Details")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
