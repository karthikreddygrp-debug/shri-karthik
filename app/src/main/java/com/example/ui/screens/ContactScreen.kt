package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.StoreConstants
import com.example.ui.components.SendToPhoneDialog
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.StoreUiState
import com.example.util.StoreIntents

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContactScreen(
    uiState: StoreUiState,
    onToggleSaveLocation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTopic by remember { mutableStateOf<String?>(null) }
    var showSendToPhone by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 960.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Store Info Card
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = NavyDeep, contentColor = CrispWhite),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElectricBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Storefront, contentDescription = null, tint = CrispWhite)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = StoreConstants.BUSINESS_NAME,
                                style = MaterialTheme.typography.titleLarge,
                                color = CrispWhite
                            )
                            Text(
                                text = StoreConstants.TAGLINE,
                                style = MaterialTheme.typography.bodySmall,
                                color = CrispWhite.copy(alpha = 0.8f)
                            )
                        }
                    }

                    InfoLine(
                        icon = Icons.Filled.LocationOn,
                        label = "Address",
                        value = StoreConstants.ADDRESS
                    )
                    InfoLine(
                        icon = Icons.Filled.PinDrop,
                        label = "Google Plus Code",
                        value = StoreConstants.PLUS_CODE
                    )
                    InfoLine(
                        icon = Icons.Filled.Call,
                        label = "Phone",
                        value = StoreConstants.PHONE_DISPLAY
                    )
                }
            }

            // Primary Action Buttons
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Quick Store Actions",
                        style = MaterialTheme.typography.titleMedium
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { StoreIntents.dialStore(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("contact_call_store")
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call Store")
                        }

                        Button(
                            onClick = { StoreIntents.openDirections(context) },
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("contact_get_directions")
                        ) {
                            Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Get Directions")
                        }

                        OutlinedButton(
                            onClick = { StoreIntents.openNearbyPlaces(context) },
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("contact_nearby_places")
                        ) {
                            Icon(Icons.Filled.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Nearby Places")
                        }

                        OutlinedButton(
                            onClick = { StoreIntents.shareStore(context) },
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("contact_share_website")
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Website")
                        }

                        OutlinedButton(
                            onClick = onToggleSaveLocation,
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("contact_save_location")
                        ) {
                            Icon(
                                imageVector = if (uiState.isLocationSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = null,
                                tint = if (uiState.isLocationSaved) ElectricBlue else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (uiState.isLocationSaved) "Location Saved" else "Save Location")
                        }

                        OutlinedButton(
                            onClick = { showSendToPhone = true },
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("contact_send_to_phone")
                        ) {
                            Icon(Icons.Filled.PhoneAndroid, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Send to Phone")
                        }
                    }

                    if (uiState.isLocationSaved) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Saved to your bookmarked store locations: ${StoreConstants.PLUS_CODE}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            // WhatsApp Enquiry Builder Card
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, WhatsAppGreen.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "WhatsApp Store Enquiry",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Select an enquiry topic (optional) and message ${StoreConstants.BUSINESS_NAME} directly on WhatsApp.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StoreConstants.ENQUIRY_TOPICS.forEach { topic ->
                            FilterChip(
                                selected = selectedTopic == topic,
                                onClick = {
                                    selectedTopic = if (selectedTopic == topic) null else topic
                                },
                                label = { Text(topic) }
                            )
                        }
                    }

                    val message = if (selectedTopic != null) {
                        "Hello ${StoreConstants.BUSINESS_NAME}, I would like to enquire about $selectedTopic."
                    } else {
                        StoreConstants.DEFAULT_WHATSAPP_MESSAGE
                    }

                    Button(
                        onClick = { StoreIntents.openWhatsAppEnquiry(context, message) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WhatsAppGreen,
                            contentColor = CrispWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("contact_whatsapp_enquiry")
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WhatsApp Enquiry (${StoreConstants.PHONE_DISPLAY})")
                    }
                }
            }

            // Interactive Google Maps Integration Card
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Interactive Store Location Map",
                        style = MaterialTheme.typography.titleMedium
                    )
                    InteractiveStoreMapView()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { StoreIntents.openDirections(context) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Directions")
                        }
                        OutlinedButton(
                            onClick = { StoreIntents.openNearbyPlaces(context) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Filled.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Nearby")
                        }
                    }
                }
            }
        }
    }

    if (showSendToPhone) {
        SendToPhoneDialog(onDismiss = { showSendToPhone = false })
    }
}

@Composable
private fun InfoLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ElectricBlue,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = CrispWhite.copy(alpha = 0.68f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = CrispWhite
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun InteractiveStoreMapView() {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var loadFailed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (!loadFailed) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                if (request?.isForMainFrame == true) {
                                    loadFailed = true
                                    isLoading = false
                                }
                            }
                        }
                        loadUrl(StoreConstants.GOOGLE_MAPS_EMBED_URL)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isLoading && !loadFailed) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        }

        if (loadFailed) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PinDrop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = StoreConstants.PLUS_CODE,
                    style = MaterialTheme.typography.titleSmall
                )
                OutlinedButton(
                    onClick = { StoreIntents.openGoogleMapsLocation(context) },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Open Interactive Map")
                }
            }
        }
    }
}
