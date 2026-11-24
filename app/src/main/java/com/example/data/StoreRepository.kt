package com.example.data

import android.content.Context
import android.net.Uri
import com.example.model.GalleryCategory
import com.example.model.StoreCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class StoreRepository(
    private val context: Context,
    private val dao: StoreDao,
    private val preferences: StorePreferencesRepository
) {
    val products: Flow<List<ProductEntity>> = dao.observeAllProducts()
    val offers: Flow<List<OfferHighlightEntity>> = dao.observeAllOffers()
    val galleryPhotos: Flow<List<GalleryPhotoEntity>> = dao.observeAllGalleryPhotos()
    val isLocationSaved: Flow<Boolean> = preferences.isLocationSaved
    val isOwnerMode: Flow<Boolean> = preferences.isOwnerMode

    suspend fun ensureInitialCatalogSeeded() {
        val alreadySeeded = preferences.isSeeded.first()
        if (!alreadySeeded && dao.getProductCount() == 0) {
            dao.insertProducts(initialProducts())
        }
        if (!alreadySeeded && dao.getOfferCount() == 0) {
            dao.insertOffers(initialHighlights())
        }
        if (!alreadySeeded && dao.getGalleryCount() == 0) {
            dao.insertGalleryPhotos(initialGalleryPhotos())
        }
        if (!alreadySeeded) {
            preferences.markSeeded()
        }
    }

    suspend fun saveProduct(product: ProductEntity) {
        if (product.id == 0L) {
            dao.insertProduct(product)
        } else {
            dao.updateProduct(product)
        }
    }

    suspend fun deleteProduct(product: ProductEntity) {
        dao.deleteProduct(product)
    }

    suspend fun saveOffer(offer: OfferHighlightEntity) {
        if (offer.id == 0L) {
            dao.insertOffer(offer)
        } else {
            dao.updateOffer(offer)
        }
    }

    suspend fun deleteOffer(offer: OfferHighlightEntity) {
        dao.deleteOffer(offer)
    }

    suspend fun addGalleryPhoto(photo: GalleryPhotoEntity) {
        dao.insertGalleryPhoto(photo)
    }

    suspend fun deleteGalleryPhoto(photo: GalleryPhotoEntity) {
        dao.deleteGalleryPhoto(photo)
    }

    suspend fun toggleSaveLocation(): Boolean = preferences.toggleSaveLocation()

    suspend fun setOwnerMode(enabled: Boolean) = preferences.setOwnerMode(enabled)

    suspend fun persistPickedImage(sourceUri: Uri): String = withContext(Dispatchers.IO) {
        val mediaDir = File(context.filesDir, "store_media").apply {
            if (!exists()) mkdirs()
        }
        val destFile = File(mediaDir, "media_${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        Uri.fromFile(destFile).toString()
    }

    private fun initialProducts(): List<ProductEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            // Electronics & Home Appliances (NO invented prices or stock status)
            ProductEntity(
                name = "Smart LED Televisions",
                brand = "Multi-Brand Showroom",
                categoryId = StoreCategory.ELECTRONICS.id,
                description = "HD, Full HD, and 4K Smart TVs for living rooms and home entertainment.",
                isFeatured = true,
                createdAt = now - 1000
            ),
            ProductEntity(
                name = "Refrigerators",
                brand = "Home Appliances",
                categoryId = StoreCategory.ELECTRONICS.id,
                description = "Single-door and double-door refrigerators suited for everyday family cooling needs.",
                isFeatured = true,
                createdAt = now - 2000
            ),
            ProductEntity(
                name = "Washing Machines",
                brand = "Home Appliances",
                categoryId = StoreCategory.ELECTRONICS.id,
                description = "Semi-automatic and fully automatic washing machines for effortless laundry care.",
                isFeatured = false,
                createdAt = now - 3000
            ),
            ProductEntity(
                name = "Air Conditioners & Fans",
                brand = "Home Cooling",
                categoryId = StoreCategory.ELECTRONICS.id,
                description = "Split air conditioners, ceiling fans, and table fans for comfortable indoor cooling.",
                isFeatured = false,
                createdAt = now - 4000
            ),
            ProductEntity(
                name = "Smartphones & Electronic Accessories",
                brand = "Mobile & Tech",
                categoryId = StoreCategory.ELECTRONICS.id,
                description = "Everyday smartphones, chargers, cables, earphones, and essential electronic accessories.",
                isFeatured = false,
                createdAt = now - 5000
            ),

            // Milky Mist Dairy Products
            ProductEntity(
                name = "Milky Mist Fresh Milk",
                brand = "Milky Mist",
                categoryId = StoreCategory.MILKY_MIST.id,
                description = "Fresh daily milk from Milky Mist for tea, coffee, and household cooking.",
                isFeatured = true,
                createdAt = now - 6000
            ),
            ProductEntity(
                name = "Milky Mist Paneer",
                brand = "Milky Mist",
                categoryId = StoreCategory.MILKY_MIST.id,
                description = "Soft and fresh Milky Mist paneer cubes and blocks ideal for curries and snacks.",
                isFeatured = true,
                createdAt = now - 7000
            ),
            ProductEntity(
                name = "Milky Mist Curd",
                brand = "Milky Mist",
                categoryId = StoreCategory.MILKY_MIST.id,
                description = "Thick, creamy Milky Mist set curd for everyday meals.",
                isFeatured = false,
                createdAt = now - 8000
            ),
            ProductEntity(
                name = "Milky Mist Butter & Cheese",
                brand = "Milky Mist",
                categoryId = StoreCategory.MILKY_MIST.id,
                description = "Cooking butter, table butter, cheese slices, and cheddar/mozzarella varieties.",
                isFeatured = false,
                createdAt = now - 9000
            ),
            ProductEntity(
                name = "Milky Mist Ghee & Dairy Range",
                brand = "Milky Mist",
                categoryId = StoreCategory.MILKY_MIST.id,
                description = "Pure aromatic Milky Mist ghee and assorted Milky Mist dairy products.",
                isFeatured = false,
                createdAt = now - 10000
            ),

            // Groceries & Packaged Foods
            ProductEntity(
                name = "Biscuits & Tea-Time Snacks",
                brand = "Grocery Selection",
                categoryId = StoreCategory.GROCERIES.id,
                description = "Assorted sweet & salted biscuits, chips, namkeen, and family snack packs.",
                isFeatured = true,
                createdAt = now - 11000
            ),
            ProductEntity(
                name = "Packaged Foods & Everyday Groceries",
                brand = "Pantry Essentials",
                categoryId = StoreCategory.GROCERIES.id,
                description = "Ready-to-cook items, noodles, breakfast essentials, and daily kitchen groceries.",
                isFeatured = false,
                createdAt = now - 12000
            ),

            // Beverages
            ProductEntity(
                name = "Soft Drinks & Fruit Juices",
                brand = "Chilled Beverages",
                categoryId = StoreCategory.BEVERAGES.id,
                description = "Chilled carbonated soft drinks, packaged fruit juices, and refreshing drinks.",
                isFeatured = true,
                createdAt = now - 13000
            ),
            ProductEntity(
                name = "Packaged Drinking Water",
                brand = "Beverages",
                categoryId = StoreCategory.BEVERAGES.id,
                description = "Sealed bottled water in convenient carry sizes and other refreshing beverages.",
                isFeatured = false,
                createdAt = now - 14000
            ),

            // Household Essentials
            ProductEntity(
                name = "Soaps, Detergents & Cleaning Care",
                brand = "Home & Personal Care",
                categoryId = StoreCategory.HOUSEHOLD.id,
                description = "Bath soaps, laundry detergents, dishwash liquids, and surface cleaners.",
                isFeatured = true,
                createdAt = now - 15000
            ),

            // Daily Essentials
            ProductEntity(
                name = "Everyday Home Necessities",
                brand = "Daily Essentials",
                categoryId = StoreCategory.DAILY_ESSENTIALS.id,
                description = "Practical everyday household necessities and general store items under one roof.",
                isFeatured = false,
                createdAt = now - 16000
            )
        )
    }

    private fun initialHighlights(): List<OfferHighlightEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            OfferHighlightEntity(
                title = "Electronics & Home Appliances Showroom",
                description = "Explore televisions, refrigerators, washing machines, air conditioners, fans, smartphones, and accessories with in-store product guidance.",
                categoryTag = "Store Highlight",
                validityNote = "Visit store or enquire on WhatsApp for current models & pricing",
                isStoreHighlight = true,
                createdAt = now - 1000
            ),
            OfferHighlightEntity(
                title = "Milky Mist Fresh Dairy Counter",
                description = "Dedicated Milky Mist dairy selection featuring fresh milk, paneer, curd, butter, cheese, and ghee.",
                categoryTag = "Dairy Highlight",
                validityNote = "Enquire for daily stock and pack sizes",
                isStoreHighlight = true,
                createdAt = now - 2000
            ),
            OfferHighlightEntity(
                title = "Everyday Groceries, Beverages & Household Care",
                description = "Pick up biscuits, snacks, packaged foods, cold drinks, soaps, detergents, and daily home necessities in one convenient stop.",
                categoryTag = "Supermarket",
                validityNote = "Conveniently located at 13-2, RC Rd, Avilala, Vedantha Puram",
                isStoreHighlight = true,
                createdAt = now - 3000
            )
        )
    }

    private fun initialGalleryPhotos(): List<GalleryPhotoEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            GalleryPhotoEntity(
                title = "Showroom & Supermarket Aisles",
                categoryId = GalleryCategory.STORE_INTERIOR.id,
                presetKey = "HERO",
                createdAt = now - 1000
            ),
            GalleryPhotoEntity(
                title = "Electronics & Home Appliances Display",
                categoryId = GalleryCategory.ELECTRONICS.id,
                presetKey = "ELECTRONICS",
                createdAt = now - 2000
            ),
            GalleryPhotoEntity(
                title = "Milky Mist Fresh Dairy Range",
                categoryId = GalleryCategory.MILKY_MIST.id,
                presetKey = "MILKY_MIST",
                createdAt = now - 3000
            ),
            GalleryPhotoEntity(
                title = "Groceries, Beverages & Household Shelves",
                categoryId = GalleryCategory.GROCERY_PRODUCTS.id,
                presetKey = "GROCERIES",
                createdAt = now - 4000
            )
        )
    }
}
