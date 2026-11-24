package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.ProductEntity
import com.example.model.StoreCategory
import com.example.model.StoreConstants
import com.example.ui.components.ProductCard
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.StoreUiState
import com.example.util.StoreIntents

@Composable
fun ProductsScreen(
    uiState: StoreUiState,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (StoreCategory?) -> Unit,
    onToggleOwnerMode: () -> Unit,
    onAddProductClick: () -> Unit,
    onEditProductClick: (ProductEntity) -> Unit,
    onDeleteProductClick: (ProductEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = when {
            maxWidth >= 900.dp -> 3
            maxWidth >= 600.dp -> 2
            else -> 1
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1120.dp)
                .align(Alignment.TopCenter)
        ) {
            // Header + Search + Owner Quick Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Product Catalogue",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${uiState.filteredProducts.size} items shown",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onToggleOwnerMode,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("products_owner_toggle")
                        ) {
                            Icon(Icons.Filled.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (uiState.isOwnerMode) "Done" else "Manage")
                        }

                        Button(
                            onClick = onAddProductClick,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("add_product_button")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add")
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search electronics, Milky Mist, groceries...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("product_search_input")
                )
            }

            // Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { onSelectCategory(null) },
                        label = { Text("All Categories") },
                        modifier = Modifier.testTag("filter_all")
                    )
                }
                items(StoreCategory.entries) { cat ->
                    FilterChip(
                        selected = uiState.selectedCategory == cat,
                        onClick = {
                            onSelectCategory(if (uiState.selectedCategory == cat) null else cat)
                        },
                        label = { Text(cat.displayName) },
                        modifier = Modifier.testTag("filter_${cat.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Inventory2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = "No matching products in catalogue",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Ask us on WhatsApp if you are looking for a specific item or brand.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        onSearchQueryChange("")
                                        onSelectCategory(null)
                                    },
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Text("Reset Filters")
                                }
                                Button(
                                    onClick = {
                                        val queryMsg = if (uiState.searchQuery.isNotBlank()) {
                                            "Hello ${StoreConstants.BUSINESS_NAME}, do you have '${uiState.searchQuery}' available?"
                                        } else {
                                            StoreConstants.DEFAULT_WHATSAPP_MESSAGE
                                        }
                                        StoreIntents.openWhatsAppEnquiry(context, queryMsg)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = WhatsAppGreen,
                                        contentColor = CrispWhite
                                    ),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ask Store")
                                }
                            }
                        }
                    }
                }
            } else {
                val chunked = uiState.filteredProducts.chunked(columns.coerceAtLeast(1))
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chunked) { rowProducts ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowProducts.forEach { product ->
                                ProductCard(
                                    product = product,
                                    isOwnerMode = uiState.isOwnerMode,
                                    onEnquireClick = {
                                        StoreIntents.openProductEnquiry(
                                            context = context,
                                            productName = product.name,
                                            brand = product.brand
                                        )
                                    },
                                    onEditClick = { onEditProductClick(product) },
                                    onDeleteClick = { onDeleteProductClick(product) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(columns - rowProducts.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
