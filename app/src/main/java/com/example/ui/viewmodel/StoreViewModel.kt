package com.example.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.GalleryPhotoEntity
import com.example.data.OfferHighlightEntity
import com.example.data.ProductEntity
import com.example.data.StoreRepository
import com.example.model.GalleryCategory
import com.example.model.StoreCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StoreUiState(
    val allProducts: List<ProductEntity> = emptyList(),
    val filteredProducts: List<ProductEntity> = emptyList(),
    val featuredProducts: List<ProductEntity> = emptyList(),
    val milkyMistProducts: List<ProductEntity> = emptyList(),
    val offers: List<OfferHighlightEntity> = emptyList(),
    val galleryPhotos: List<GalleryPhotoEntity> = emptyList(),
    val filteredGalleryPhotos: List<GalleryPhotoEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: StoreCategory? = null,
    val selectedGalleryCategory: GalleryCategory? = null,
    val isLocationSaved: Boolean = false,
    val isOwnerMode: Boolean = false,
    val isLoading: Boolean = true
)

class StoreViewModel(
    private val repository: StoreRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow<StoreCategory?>(null)
    private val selectedGalleryCategory = MutableStateFlow<GalleryCategory?>(null)
    private val isInitialized = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.ensureInitialCatalogSeeded()
            isInitialized.value = true
        }
    }

    private val filterFlow = combine(
        searchQuery,
        selectedCategory,
        selectedGalleryCategory,
        isInitialized
    ) { query, category, galleryCat, initialized ->
        FilterParams(query, category, galleryCat, initialized)
    }

    private val prefsFlow = combine(
        repository.isLocationSaved,
        repository.isOwnerMode
    ) { saved, owner -> saved to owner }

    val uiState: StateFlow<StoreUiState> = combine(
        repository.products,
        repository.offers,
        repository.galleryPhotos,
        filterFlow,
        prefsFlow
    ) { products, offers, gallery, filters, prefs ->
        val queryTrimmed = filters.query.trim()
        val filteredProducts = products.filter { product ->
            val matchesCat = filters.category == null || product.categoryId == filters.category.id
            val matchesQuery = queryTrimmed.isEmpty() ||
                product.name.contains(queryTrimmed, ignoreCase = true) ||
                product.brand.contains(queryTrimmed, ignoreCase = true) ||
                product.description.contains(queryTrimmed, ignoreCase = true) ||
                StoreCategory.fromId(product.categoryId).displayName.contains(queryTrimmed, ignoreCase = true)
            matchesCat && matchesQuery
        }
        val featuredProducts = products.filter { it.isFeatured }.ifEmpty { products.take(6) }
        val milkyMistProducts = products.filter { it.categoryId == StoreCategory.MILKY_MIST.id }
        val filteredGallery = gallery.filter { photo ->
            filters.galleryCat == null || photo.categoryId == filters.galleryCat.id
        }

        StoreUiState(
            allProducts = products,
            filteredProducts = filteredProducts,
            featuredProducts = featuredProducts,
            milkyMistProducts = milkyMistProducts,
            offers = offers,
            galleryPhotos = gallery,
            filteredGalleryPhotos = filteredGallery,
            searchQuery = filters.query,
            selectedCategory = filters.category,
            selectedGalleryCategory = filters.galleryCat,
            isLocationSaved = prefs.first,
            isOwnerMode = prefs.second,
            isLoading = !filters.initialized
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StoreUiState()
    )

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun selectCategory(category: StoreCategory?) {
        selectedCategory.value = category
    }

    fun selectGalleryCategory(category: GalleryCategory?) {
        selectedGalleryCategory.value = category
    }

    fun toggleOwnerMode() {
        viewModelScope.launch {
            repository.setOwnerMode(!uiState.value.isOwnerMode)
        }
    }

    fun setOwnerMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setOwnerMode(enabled)
        }
    }

    fun toggleSaveLocation(onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val saved = repository.toggleSaveLocation()
            onResult(saved)
        }
    }

    fun saveProduct(
        existingId: Long = 0L,
        name: String,
        brand: String,
        category: StoreCategory,
        description: String,
        priceText: String,
        availabilityText: String,
        pickedImageUri: Uri?,
        existingImageUri: String,
        isFeatured: Boolean
    ) {
        viewModelScope.launch {
            val finalUri = if (pickedImageUri != null) {
                repository.persistPickedImage(pickedImageUri)
            } else {
                existingImageUri
            }
            repository.saveProduct(
                ProductEntity(
                    id = existingId,
                    name = name.trim(),
                    brand = brand.trim(),
                    categoryId = category.id,
                    description = description.trim(),
                    priceText = priceText.trim(),
                    availabilityText = availabilityText.trim(),
                    imageUri = finalUri,
                    isFeatured = isFeatured
                )
            )
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun saveOffer(
        existingId: Long = 0L,
        title: String,
        description: String,
        categoryTag: String,
        validityNote: String,
        isStoreHighlight: Boolean
    ) {
        viewModelScope.launch {
            repository.saveOffer(
                OfferHighlightEntity(
                    id = existingId,
                    title = title.trim(),
                    description = description.trim(),
                    categoryTag = categoryTag.trim().ifEmpty { "Store Offer" },
                    validityNote = validityNote.trim(),
                    isStoreHighlight = isStoreHighlight
                )
            )
        }
    }

    fun deleteOffer(offer: OfferHighlightEntity) {
        viewModelScope.launch {
            repository.deleteOffer(offer)
        }
    }

    fun addGalleryPhoto(title: String, category: GalleryCategory, pickedImageUri: Uri) {
        viewModelScope.launch {
            val persisted = repository.persistPickedImage(pickedImageUri)
            repository.addGalleryPhoto(
                GalleryPhotoEntity(
                    title = title.trim().ifEmpty { category.displayName },
                    categoryId = category.id,
                    imageUri = persisted
                )
            )
        }
    }

    fun deleteGalleryPhoto(photo: GalleryPhotoEntity) {
        viewModelScope.launch {
            repository.deleteGalleryPhoto(photo)
        }
    }

    private data class FilterParams(
        val query: String,
        val category: StoreCategory?,
        val galleryCat: GalleryCategory?,
        val initialized: Boolean
    )

    class Factory(private val repository: StoreRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StoreViewModel(repository) as T
        }
    }
}
