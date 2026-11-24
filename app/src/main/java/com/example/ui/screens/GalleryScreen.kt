package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.R
import com.example.data.GalleryPhotoEntity
import com.example.model.GalleryCategory
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.NavyDeep
import com.example.ui.viewmodel.StoreUiState

@Composable
fun GalleryScreen(
    uiState: StoreUiState,
    onSelectGalleryCategory: (GalleryCategory?) -> Unit,
    onAddGalleryPhoto: (title: String, category: GalleryCategory, uri: Uri) -> Unit,
    onDeleteGalleryPhoto: (GalleryPhotoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var pendingPickedUri by remember { mutableStateOf<Uri?>(null) }
    var previewPhoto by remember { mutableStateOf<GalleryPhotoEntity?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            pendingPickedUri = uri
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = when {
            maxWidth >= 900.dp -> 3
            maxWidth >= 560.dp -> 2
            else -> 2
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1120.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Store Gallery",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Exterior, interior, electronics, Milky Mist & grocery displays",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("gallery_upload_button")
                ) {
                    Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload")
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedGalleryCategory == null,
                        onClick = { onSelectGalleryCategory(null) },
                        label = { Text("All Photos") }
                    )
                }
                items(GalleryCategory.entries) { cat ->
                    FilterChip(
                        selected = uiState.selectedGalleryCategory == cat,
                        onClick = {
                            onSelectGalleryCategory(if (uiState.selectedGalleryCategory == cat) null else cat)
                        },
                        label = { Text(cat.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.filteredGalleryPhotos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "No photos in this category yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Tap Upload to add store or product photos from your device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val rows = uiState.filteredGalleryPhotos.chunked(columns.coerceAtLeast(1))
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(rows) { rowPhotos ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowPhotos.forEach { photo ->
                                GalleryPhotoCard(
                                    photo = photo,
                                    canDelete = uiState.isOwnerMode || photo.imageUri.isNotBlank(),
                                    onClick = { previewPhoto = photo },
                                    onDelete = { onDeleteGalleryPhoto(photo) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(columns - rowPhotos.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }

    pendingPickedUri?.let { uri ->
        AddGalleryPhotoDialog(
            pickedUri = uri,
            onDismiss = { pendingPickedUri = null },
            onConfirm = { title, category ->
                onAddGalleryPhoto(title, category, uri)
                pendingPickedUri = null
            }
        )
    }

    previewPhoto?.let { photo ->
        Dialog(onDismissRequest = { previewPhoto = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        GalleryPhotoImage(
                            photo = photo,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { previewPhoto = null },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(NavyDeep.copy(alpha = 0.72f))
                                .minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close preview",
                                tint = CrispWhite
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = GalleryCategory.fromId(photo.categoryId).displayName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = photo.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryPhotoCard(
    photo: GalleryPhotoEntity,
    canDelete: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = GalleryCategory.fromId(photo.categoryId)
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("gallery_card_${photo.id}"),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
            ) {
                GalleryPhotoImage(
                    photo = photo,
                    modifier = Modifier.fillMaxSize()
                )
                if (canDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(NavyDeep.copy(alpha = 0.72f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Remove photo",
                                tint = CrispWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun GalleryPhotoImage(
    photo: GalleryPhotoEntity,
    modifier: Modifier = Modifier
) {
    if (photo.imageUri.isNotBlank()) {
        AsyncImage(
            model = Uri.parse(photo.imageUri),
            contentDescription = photo.title,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        val drawableRes = when (photo.presetKey) {
            "ELECTRONICS" -> R.drawable.img_cat_electronics_1795534493306
            "MILKY_MIST" -> R.drawable.img_cat_milky_mist_1795534494498
            "GROCERIES" -> R.drawable.img_cat_groceries_essentials_1795534495627
            else -> R.drawable.img_hero_showroom_1795534492675
        }
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = photo.title,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun AddGalleryPhotoDialog(
    pickedUri: Uri,
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: GalleryCategory) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(GalleryCategory.STORE_INTERIOR) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Store Gallery Photo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AsyncImage(
                    model = pickedUri,
                    contentDescription = "Selected store photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Photo Caption") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Gallery Section", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GalleryCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.displayName) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, selectedCategory) }) {
                Text("Add to Gallery")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
