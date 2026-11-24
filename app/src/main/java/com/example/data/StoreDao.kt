package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Query("SELECT * FROM products ORDER BY isFeatured DESC, createdAt DESC")
    fun observeAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT * FROM offers ORDER BY isStoreHighlight ASC, createdAt DESC")
    fun observeAllOffers(): Flow<List<OfferHighlightEntity>>

    @Query("SELECT COUNT(*) FROM offers")
    suspend fun getOfferCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: OfferHighlightEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffers(offers: List<OfferHighlightEntity>)

    @Update
    suspend fun updateOffer(offer: OfferHighlightEntity)

    @Delete
    suspend fun deleteOffer(offer: OfferHighlightEntity)

    @Query("SELECT * FROM gallery_photos ORDER BY createdAt DESC")
    fun observeAllGalleryPhotos(): Flow<List<GalleryPhotoEntity>>

    @Query("SELECT COUNT(*) FROM gallery_photos")
    suspend fun getGalleryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryPhoto(photo: GalleryPhotoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryPhotos(photos: List<GalleryPhotoEntity>)

    @Delete
    suspend fun deleteGalleryPhoto(photo: GalleryPhotoEntity)
}
