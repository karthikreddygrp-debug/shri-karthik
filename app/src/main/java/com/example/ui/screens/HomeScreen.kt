package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.OfferHighlightEntity
import com.example.data.ProductEntity
import com.example.model.StoreCategory
import com.example.model.StoreConstants
import com.example.ui.components.ProductCard
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueLight
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.StoreUiState
import com.example.util.StoreIntents

enum class HomeScrollTarget {
    HERO,
    CATEGORIES,
    ABOUT
}

@Composable
fun HomeScreen(
    uiState: StoreUiState,
    scrollTargetRequest: Pair<HomeScrollTarget, Int>?,
    onExploreProducts: () -> Unit,
    onExploreCategory: (StoreCategory) -> Unit,
    onNavigateContact: () -> Unit,
    onNavigateGallery: () -> Unit,
    onEditProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit,
    onAddOffer: () -> Unit,
    onEditOffer: (OfferHighlightEntity) -> Unit,
    onDeleteOffer: (OfferHighlightEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState: LazyListState = rememberLazyListState()

    // Scroll to specific home section when requested from the sticky Quick Nav strip
    LaunchedEffect(scrollTargetRequest) {
        val target = scrollTargetRequest?.first ?: return@LaunchedEffect
        val index = when (target) {
            HomeScrollTarget.HERO -> 0
            HomeScrollTarget.CATEGORIES -> 1
            HomeScrollTarget.ABOUT -> 5
        }
        listState.animateScrollToItem(index)
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = when {
            maxWidth >= 900.dp -> 3
            maxWidth >= 600.dp -> 2
            else -> 1
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1120.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            // 0: Hero Section
            item(key = "hero") {
                HeroSection(
                    isWide = columns >= 2,
                    onExploreProducts = onExploreProducts,
                    onContactUs = onNavigateContact
                )
            }

            // 1: Shop by Category
            item(key = "categories") {
                ShopByCategorySection(
                    columns = columns,
                    onExploreCategory = onExploreCategory
                )
            }

            // 2: Featured Products
            item(key = "featured") {
                FeaturedProductsSection(
                    featuredProducts = uiState.featuredProducts,
                    isOwnerMode = uiState.isOwnerMode,
                    onViewAll = onExploreProducts,
                    onEnquire = { product ->
                        StoreIntents.openProductEnquiry(context, product.name, product.brand)
                    },
                    onEdit = onEditProduct,
                    onDelete = onDeleteProduct
                )
            }

            // 3: Special Offers & Store Highlights
            item(key = "offers") {
                SpecialOffersSection(
                    offers = uiState.offers,
                    isOwnerMode = uiState.isOwnerMode,
                    onAddOffer = onAddOffer,
                    onEditOffer = onEditOffer,
                    onDeleteOffer = onDeleteOffer,
                    onEnquireOffer = { offer ->
                        StoreIntents.openWhatsAppEnquiry(
                            context,
                            "Hello ${StoreConstants.BUSINESS_NAME}, I would like to enquire about: ${offer.title}."
                        )
                    }
                )
            }

            // 4: Dedicated Milky Mist Section
            item(key = "milky_mist") {
                DedicatedMilkyMistSection(
                    milkyMistProducts = uiState.milkyMistProducts,
                    onEnquireProduct = { product ->
                        StoreIntents.openProductEnquiry(context, product.name, product.brand)
                    },
                    onExploreMilkyMist = { onExploreCategory(StoreCategory.MILKY_MIST) }
                )
            }

            // 5: About Us
            item(key = "about") {
                AboutUsSection()
            }

            // 6: Why Choose Us
            item(key = "why_choose_us") {
                WhyChooseUsSection(columns = if (columns == 1) 1 else columns)
            }

            // 7: Footer
            item(key = "footer") {
                StoreFooter(
                    onNavigateHome = { /* already home */ },
                    onNavigateProducts = onExploreProducts,
                    onNavigateGallery = onNavigateGallery,
                    onNavigateContact = onNavigateContact,
                    onSelectCategory = onExploreCategory
                )
            }
        }
    }
}

@Composable
private fun HeroSection(
    isWide: Boolean,
    onExploreProducts: () -> Unit,
    onContactUs: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.42f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(NavyDeep, NavyPrimary)
                )
            )
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = glowPulse),
                            ElectricBlue.copy(alpha = 0f)
                        ),
                        center = Offset(size.width * 0.85f, size.height * 0.2f),
                        radius = size.maxDimension * 0.65f
                    )
                )
            }
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeroTextAndActions(
                    onExploreProducts = onExploreProducts,
                    onContactUs = onContactUs,
                    modifier = Modifier.weight(1.1f)
                )
                HeroBannerImage(
                    modifier = Modifier
                        .weight(0.9f)
                        .height(250.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                HeroTextAndActions(
                    onExploreProducts = onExploreProducts,
                    onContactUs = onContactUs
                )
                HeroBannerImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                )
            }
        }
    }
}

@Composable
private fun HeroTextAndActions(
    onExploreProducts: () -> Unit,
    onContactUs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Surface(
            color = ElectricBlue.copy(alpha = 0.22f),
            shape = RoundedCornerShape(999.dp),
            border = BorderStroke(1.dp, ElectricBlueLight.copy(alpha = 0.45f))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Storefront,
                    contentDescription = null,
                    tint = ElectricBlueLight,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Vedantha Puram · Multi-Category Retail",
                    style = MaterialTheme.typography.labelMedium,
                    color = CrispWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = StoreConstants.BUSINESS_NAME,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
            color = CrispWhite
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = StoreConstants.HERO_SUBTITLE,
            style = MaterialTheme.typography.titleMedium,
            color = ElectricBlueLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = StoreConstants.HERO_SUPPORTING,
            style = MaterialTheme.typography.bodyMedium,
            color = CrispWhite.copy(alpha = 0.84f)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onExploreProducts,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricBlue,
                    contentColor = CrispWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("hero_explore_products")
            ) {
                Text("Explore Products")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }

            OutlinedButton(
                onClick = onContactUs,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CrispWhite),
                border = BorderStroke(1.dp, CrispWhite.copy(alpha = 0.55f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("hero_contact_us")
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Contact Us")
            }
        }
    }
}

@Composable
private fun HeroBannerImage(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CrispWhite.copy(alpha = 0.18f)),
        shadowElevation = 6.dp
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_hero_showroom_1795534492675),
            contentDescription = "SHRI KARTHIK ENTERPRISE electronics showroom and supermarket interior",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ShopByCategorySection(
    columns: Int,
    onExploreCategory: (StoreCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "Shop by Category",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Browse electronics, fresh Milky Mist dairy, groceries, beverages & home care",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val rows = StoreCategory.entries.chunked(columns.coerceAtLeast(1))
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { category ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("category_card_${category.id}"),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = category.imageRes),
                                contentDescription = category.displayName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(136.dp)
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = category.shortDescription,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    category.includedItems.forEach { itemName ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = itemName,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { onExploreCategory(category) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("explore_category_${category.id}")
                                ) {
                                    Text("Explore Category")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
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

@Composable
private fun FeaturedProductsSection(
    featuredProducts: List<ProductEntity>,
    isOwnerMode: Boolean,
    onViewAll: () -> Unit,
    onEnquire: (ProductEntity) -> Unit,
    onEdit: (ProductEntity) -> Unit,
    onDelete: (ProductEntity) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Featured Products",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Highlights across electronics, Milky Mist dairy, groceries & home essentials",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onViewAll) {
                Text("View All")
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(featuredProducts, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    isOwnerMode = isOwnerMode,
                    onEnquireClick = { onEnquire(product) },
                    onEditClick = { onEdit(product) },
                    onDeleteClick = { onDelete(product) },
                    modifier = Modifier.width(272.dp)
                )
            }
        }
    }
}
