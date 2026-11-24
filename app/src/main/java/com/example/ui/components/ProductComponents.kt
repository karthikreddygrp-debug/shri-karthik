package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.OfferHighlightEntity
import com.example.data.ProductEntity
import com.example.model.StoreCategory
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.WhatsAppGreen

@Composable
fun ProductImage(
    imageUri: String,
    category: StoreCategory,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    if (imageUri.isNotBlank()) {
        AsyncImage(
            model = Uri.parse(imageUri),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(id = category.imageRes),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    isOwnerMode: Boolean,
    onEnquireClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = StoreCategory.fromId(product.categoryId)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                ProductImage(
                    imageUri = product.imageUri,
                    category = category,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = CrispWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (product.isFeatured) {
                    Surface(
                        color = ElectricBlue,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = CrispWhite,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Featured",
                                style = MaterialTheme.typography.labelSmall,
                                color = CrispWhite
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                // Only display price or availability if explicitly provided by business owner
                if (product.priceText.isNotBlank() || product.availabilityText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (product.priceText.isNotBlank()) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = product.priceText,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        if (product.availabilityText.isNotBlank()) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = product.availabilityText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onEnquireClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WhatsAppGreen,
                            contentColor = CrispWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("enquire_product_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Enquire Now",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    if (isOwnerMode) {
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("edit_product_${product.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit ${product.name}",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("delete_product_${product.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Delete ${product.name}",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductEditorDialog(
    initialProduct: ProductEntity?,
    defaultCategory: StoreCategory = StoreCategory.ELECTRONICS,
    onDismiss: () -> Unit,
    onSave: (
        existingId: Long,
        name: String,
        brand: String,
        category: StoreCategory,
        description: String,
        priceText: String,
        availabilityText: String,
        pickedImageUri: Uri?,
        existingImageUri: String,
        isFeatured: Boolean
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var brand by remember { mutableStateOf(initialProduct?.brand ?: "") }
    var category by remember {
        mutableStateOf(
            initialProduct?.let { StoreCategory.fromId(it.categoryId) } ?: defaultCategory
        )
    }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var priceText by remember { mutableStateOf(initialProduct?.priceText ?: "") }
    var availabilityText by remember { mutableStateOf(initialProduct?.availabilityText ?: "") }
    var isFeatured by remember { mutableStateOf(initialProduct?.isFeatured ?: false) }
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var existingUri by remember { mutableStateOf(initialProduct?.imageUri ?: "") }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pickedUri = uri
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialProduct == null) "Add Store Product" else "Edit Product",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StoreCategory.entries.forEach { item ->
                        FilterChip(
                            selected = category == item,
                            onClick = { category = item },
                            label = { Text(item.displayName) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price (Optional — leave blank if on enquiry)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = availabilityText,
                    onValueChange = { availabilityText = it },
                    label = { Text("Availability Status (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Show in Featured Products",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = isFeatured,
                        onCheckedChange = { isFeatured = it }
                    )
                }

                OutlinedButton(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            pickedUri != null -> "New Photo Selected"
                            existingUri.isNotBlank() -> "Replace Product Photo"
                            else -> "Choose Product Photo"
                        }
                    )
                }

                if (pickedUri != null || existingUri.isNotBlank()) {
                    AsyncImage(
                        model = pickedUri ?: Uri.parse(existingUri),
                        contentDescription = "Selected product preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    if (existingUri.isNotBlank() && pickedUri == null) {
                        TextButton(onClick = { existingUri = "" }) {
                            Text("Reset to Category Image")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        initialProduct?.id ?: 0L,
                        name,
                        brand.ifBlank { category.displayName },
                        category,
                        description.ifBlank { "${category.displayName} item available at ${com.example.model.StoreConstants.BUSINESS_NAME}." },
                        priceText,
                        availabilityText,
                        pickedUri,
                        existingUri,
                        isFeatured
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text("Save Product")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun OfferEditorDialog(
    initialOffer: OfferHighlightEntity?,
    onDismiss: () -> Unit,
    onSave: (
        existingId: Long,
        title: String,
        description: String,
        categoryTag: String,
        validityNote: String,
        isStoreHighlight: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf(initialOffer?.title ?: "") }
    var description by remember { mutableStateOf(initialOffer?.description ?: "") }
    var categoryTag by remember { mutableStateOf(initialOffer?.categoryTag ?: "Seasonal Offer") }
    var validityNote by remember { mutableStateOf(initialOffer?.validityNote ?: "") }
    var isStoreHighlight by remember { mutableStateOf(initialOffer?.isStoreHighlight ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialOffer == null) "Add Offer or Highlight" else "Edit Offer / Highlight",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = categoryTag,
                    onValueChange = { categoryTag = it },
                    label = { Text("Label (e.g., Seasonal Offer, Store Highlight)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details *") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = validityNote,
                    onValueChange = { validityNote = it },
                    label = { Text("Validity / Store Note (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mark as Store Highlight", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isStoreHighlight, onCheckedChange = { isStoreHighlight = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        initialOffer?.id ?: 0L,
                        title,
                        description,
                        categoryTag,
                        validityNote,
                        isStoreHighlight
                    )
                },
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
