package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.StoreConstants
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.WhatsAppGreen

@Composable
fun StoreStickyTopBar(
    isOwnerMode: Boolean,
    isWideScreen: Boolean,
    onLogoClick: () -> Unit,
    onSearchClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onCallNowClick: () -> Unit,
    onToggleOwnerMode: () -> Unit,
    onQuickNavHome: () -> Unit,
    onQuickNavProducts: () -> Unit,
    onQuickNavCategories: () -> Unit,
    onQuickNavAbout: () -> Unit,
    onQuickNavContact: () -> Unit
) {
    Surface(
        color = NavyDeep,
        contentColor = CrispWhite,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Logo + Name
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onLogoClick)
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(ElectricBlue, ElectricBlueLight)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Storefront,
                            contentDescription = "SHRI KARTHIK ENTERPRISE logo",
                            tint = CrispWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = StoreConstants.BUSINESS_NAME,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = CrispWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = StoreConstants.TAGLINE,
                            style = MaterialTheme.typography.labelSmall,
                            color = CrispWhite.copy(alpha = 0.78f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Action buttons: Search, WhatsApp, Call Now, Owner Mode
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("top_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search products",
                            tint = CrispWhite
                        )
                    }

                    IconButton(
                        onClick = onWhatsAppClick,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("top_whatsapp_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(WhatsAppGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Chat,
                                contentDescription = "WhatsApp enquiry",
                                tint = CrispWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (isWideScreen) {
                        Button(
                            onClick = onCallNowClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricBlue,
                                contentColor = CrispWhite
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(40.dp)
                                .testTag("top_call_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Call Now",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onCallNowClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("top_call_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = "Call Now",
                                    tint = CrispWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onToggleOwnerMode,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("top_owner_toggle")
                    ) {
                        Icon(
                            imageVector = if (isOwnerMode) Icons.Filled.EditNote else Icons.Outlined.EditNote,
                            contentDescription = if (isOwnerMode) "Exit Owner Edit Mode" else "Owner Manage Mode",
                            tint = if (isOwnerMode) ElectricBlueLight else CrispWhite.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            HorizontalDivider(color = NavyPrimary, thickness = 1.dp)

            // Sticky Website-Style Quick Navigation Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyPrimary)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickNavPill("Home", onClick = onQuickNavHome, testTag = "quick_nav_home")
                QuickNavPill("Products", onClick = onQuickNavProducts, testTag = "quick_nav_products")
                QuickNavPill("Categories", onClick = onQuickNavCategories, testTag = "quick_nav_categories")
                QuickNavPill("About Us", onClick = onQuickNavAbout, testTag = "quick_nav_about")
                QuickNavPill("Contact", onClick = onQuickNavContact, testTag = "quick_nav_contact")
                if (isOwnerMode) {
                    Surface(
                        color = ElectricBlue,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Owner Mode Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = CrispWhite,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickNavPill(
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        color = NavyDeep.copy(alpha = 0.65f),
        contentColor = CrispWhite,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = CrispWhite.copy(alpha = 0.92f),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
