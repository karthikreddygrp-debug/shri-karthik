package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val brand: String,
    val categoryId: String,
    val description: String,
    val priceText: String = "",
    val availabilityText: String = "",
    val imageUri: String = "",
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "offers")
data class OfferHighlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val categoryTag: String,
    val validityNote: String = "",
    val isStoreHighlight: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "gallery_photos")
data class GalleryPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val categoryId: String,
    val imageUri: String = "",
    val presetKey: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
