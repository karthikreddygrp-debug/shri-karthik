package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.OfferHighlightEntity
import com.example.data.ProductEntity
import com.example.model.StoreCategory
import com.example.model.StoreConstants
import com.example.ui.components.ProductImage
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.DairyAccent
import com.example.ui.theme.DairySkyEnd
import com.example.ui.theme.DairySkyStart
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.WhatsAppGreen
import com.example.util.StoreIntents

@Composable
fun SpecialOffersSection(
    offers: List<OfferHighlightEntity>,
    isOwnerMode: Boolean,
    onAddOffer: () -> Unit,
    onEditOffer: (OfferHighlightEntity) -> Unit,
    onDeleteOffer: (OfferHighlightEntity) -> Unit,
    onEnquireOffer: (OfferHighlightEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Special Offers & Store Highlights",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Current store highlights and owner-posted seasonal updates",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isOwnerMode) {
                Button(
                    onClick = onAddOffer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("add_offer_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Offer")
                }
            }
        }

        offers.forEach { offer ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocalOffer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = offer.categoryTag,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        if (isOwnerMode) {
                            Row {
                                IconButton(
                                    onClick = { onEditOffer(offer) },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit offer",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteOffer(offer) },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DeleteOutline,
                                        contentDescription = "Delete offer",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = offer.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = offer.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (offer.validityNote.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = offer.validityNote,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { onEnquireOffer(offer) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Chat,
                            contentDescription = null,
                            tint = WhatsAppGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enquire on WhatsApp")
                    }
                }
            }
        }
    }
}

@Composable
fun DedicatedMilkyMistSection(
    milkyMistProducts: List<ProductEntity>,
    onEnquireProduct: (ProductEntity) -> Unit,
    onExploreMilkyMist: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val containerBrush = if (isDark) {
        Brush.linearGradient(colors = listOf(DarkSurface, DarkSurfaceVariant))
    } else {
        Brush.linearGradient(colors = listOf(DairySkyStart, DairySkyEnd))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, DairyAccent.copy(alpha = 0.25f)),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(containerBrush)
                .padding(vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(DairyAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.WaterDrop,
                            contentDescription = null,
                            tint = CrispWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Fresh Dairy Products from Milky Mist",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Milk, Paneer, Curd, Butter, Cheese, Ghee & more",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Image(
                painter = painterResource(id = R.drawable.img_cat_milky_mist_1795534494498),
                contentDescription = "Fresh Milky Mist dairy assortment",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .height(170.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(milkyMistProducts, key = { it.id }) { product ->
                    Card(
                        modifier = Modifier.width(235.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column {
                            ProductImage(
                                imageUri = product.imageUri,
                                category = StoreCategory.MILKY_MIST,
                                contentDescription = product.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp)
                            )
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = product.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = product.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (product.priceText.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = product.priceText,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = DairyAccent
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { onEnquireProduct(product) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DairyAccent,
                                        contentColor = CrispWhite
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Chat,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Enquire")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            TextButton(
                onClick = onExploreMilkyMist,
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .height(48.dp)
                    .testTag("explore_milky_mist_button")
            ) {
                Text("View All Milky Mist Products")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun AboutUsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "About Us",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = StoreConstants.BUSINESS_NAME,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${StoreConstants.BUSINESS_NAME} is a local multi-category retail store located in Vedantha Puram, Andhra Pradesh. Designed to bring the convenience of an electronics showroom and a neighbourhood supermarket together, our store helps families and individuals find everyday essentials under one roof.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Customers can visit us to explore electronics and home appliances, fresh Milky Mist dairy products, groceries, packaged foods, beverages, household essentials, and daily-use necessities with friendly in-store guidance.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class WhyChooseItem(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun WhyChooseUsSection(columns: Int) {
    val reasons = listOf(
        WhyChooseItem(
            "Multiple Product Categories",
            "Electronics, Milky Mist dairy, groceries, beverages, and household essentials in one store.",
            Icons.Filled.Category
        ),
        WhyChooseItem(
            "Convenient Shopping",
            "Pick up home appliances and daily household groceries together on a single visit.",
            Icons.Filled.ShoppingBag
        ),
        WhyChooseItem(
            "Quality Products",
            "Carefully stocked items across electronics, fresh dairy, packaged foods, and home care.",
            Icons.Filled.Verified
        ),
        WhyChooseItem(
            "Customer-Friendly Service",
            "Approachable store support for product enquiries by visit, phone, or WhatsApp.",
            Icons.Filled.Handshake
        ),
        WhyChooseItem(
            "Convenient Store Location",
            "Easy-to-reach address at 13-2, RC Rd, Avilala, Vedantha Puram.",
            Icons.Filled.LocationOn
        ),
        WhyChooseItem(
            "Assistance with Product Selection",
            "Guidance to help you choose appliances and everyday items suited to your home.",
            Icons.Filled.SupportAgent
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Why Choose Us",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        val rows = reasons.chunked(columns.coerceAtLeast(1))
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoreFooter(
    onNavigateHome: () -> Unit,
    onNavigateProducts: () -> Unit,
    onNavigateGallery: () -> Unit,
    onNavigateContact: () -> Unit,
    onSelectCategory: (StoreCategory) -> Unit
) {
    val context = LocalContext.current

    Surface(
        color = NavyDeep,
        contentColor = CrispWhite,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = StoreConstants.BUSINESS_NAME,
                    style = MaterialTheme.typography.titleLarge,
                    color = CrispWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = StoreConstants.TAGLINE,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElectricBlueLight
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Store Address",
                    style = MaterialTheme.typography.labelLarge,
                    color = CrispWhite
                )
                Text(
                    text = StoreConstants.ADDRESS,
                    style = MaterialTheme.typography.bodySmall,
                    color = CrispWhite.copy(alpha = 0.8f)
                )
                Text(
                    text = "Plus Code: ${StoreConstants.PLUS_CODE}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CrispWhite.copy(alpha = 0.8f)
                )
                Text(
                    text = "Phone: ${StoreConstants.PHONE_DISPLAY}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CrispWhite.copy(alpha = 0.9f)
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { StoreIntents.dialStore(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Store")
                }
                Button(
                    onClick = { StoreIntents.openWhatsAppEnquiry(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp")
                }
                OutlinedButton(
                    onClick = { StoreIntents.openDirections(context) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrispWhite),
                    border = BorderStroke(1.dp, CrispWhite.copy(alpha = 0.4f)),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Google Maps")
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Quick Navigation",
                    style = MaterialTheme.typography.labelLarge,
                    color = CrispWhite
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TextButton(onClick = onNavigateHome) {
                        Text("Home", color = CrispWhite.copy(alpha = 0.9f))
                    }
                    TextButton(onClick = onNavigateProducts) {
                        Text("Products", color = CrispWhite.copy(alpha = 0.9f))
                    }
                    TextButton(onClick = onNavigateGallery) {
                        Text("Gallery", color = CrispWhite.copy(alpha = 0.9f))
                    }
                    TextButton(onClick = onNavigateContact) {
                        Text("Contact", color = CrispWhite.copy(alpha = 0.9f))
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Product Categories",
                    style = MaterialTheme.typography.labelLarge,
                    color = CrispWhite
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StoreCategory.entries.forEach { cat ->
                        AssistChip(
                            onClick = { onSelectCategory(cat) },
                            label = {
                                Text(
                                    text = cat.displayName,
                                    color = CrispWhite,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            border = BorderStroke(1.dp, NavyPrimary)
                        )
                    }
                }
            }

            HorizontalDivider(color = NavyPrimary)

            Text(
                text = "© 2026 ${StoreConstants.BUSINESS_NAME}. Vedantha Puram, Andhra Pradesh.",
                style = MaterialTheme.typography.bodySmall,
                color = CrispWhite.copy(alpha = 0.65f)
            )
        }
    }
}
