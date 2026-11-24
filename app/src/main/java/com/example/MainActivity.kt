package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.OfferHighlightEntity
import com.example.data.ProductEntity
import com.example.data.StoreDatabase
import com.example.data.StorePreferencesRepository
import com.example.data.StoreRepository
import com.example.model.StoreCategory
import com.example.ui.components.OfferEditorDialog
import com.example.ui.components.ProductEditorDialog
import com.example.ui.components.StoreStickyTopBar
import com.example.ui.screens.ContactScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HomeScrollTarget
import com.example.ui.screens.ProductsScreen
import com.example.ui.theme.ShriKarthikTheme
import com.example.ui.viewmodel.StoreViewModel
import com.example.util.StoreIntents
import kotlinx.coroutines.launch

object Routes {
    const val HOME = "home"
    const val PRODUCTS = "products"
    const val GALLERY = "gallery"
    const val CONTACT = "contact"
}

private data class NavTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val APP_TABS = listOf(
    NavTab(Routes.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_tab_home"),
    NavTab(Routes.PRODUCTS, "Products", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag, "nav_tab_products"),
    NavTab(Routes.GALLERY, "Gallery", Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary, "nav_tab_gallery"),
    NavTab(Routes.CONTACT, "Contact", Icons.Filled.Call, Icons.Outlined.Call, "nav_tab_contact")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = StoreDatabase.getInstance(applicationContext)
        val prefs = StorePreferencesRepository(applicationContext)
        val repository = StoreRepository(
            context = applicationContext,
            dao = db.storeDao(),
            preferences = prefs
        )

        setContent {
            ShriKarthikTheme {
                val storeViewModel: StoreViewModel = viewModel(
                    factory = StoreViewModel.Factory(repository)
                )
                ShriKarthikApp(viewModel = storeViewModel)
            }
        }
    }
}

@Composable
fun ShriKarthikApp(viewModel: StoreViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var showProductDialog by remember { mutableStateOf(false) }

    var editingOffer by remember { mutableStateOf<OfferHighlightEntity?>(null) }
    var showOfferDialog by remember { mutableStateOf(false) }

    var scrollRequestCounter by remember { mutableIntStateOf(0) }
    var scrollRequestTarget by remember { mutableStateOf<Pair<HomeScrollTarget, Int>?>(null) }

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun jumpToHomeSection(target: HomeScrollTarget) {
        navigateToTab(Routes.HOME)
        scrollRequestCounter += 1
        scrollRequestTarget = target to scrollRequestCounter
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                StoreStickyTopBar(
                    isOwnerMode = uiState.isOwnerMode,
                    isWideScreen = isWideScreen,
                    onLogoClick = { jumpToHomeSection(HomeScrollTarget.HERO) },
                    onSearchClick = { navigateToTab(Routes.PRODUCTS) },
                    onWhatsAppClick = { StoreIntents.openWhatsAppEnquiry(context) },
                    onCallNowClick = { StoreIntents.dialStore(context) },
                    onToggleOwnerMode = {
                        val nextState = !uiState.isOwnerMode
                        viewModel.setOwnerMode(nextState)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (nextState) {
                                    "Owner Mode enabled: add, edit, or delete products & offers."
                                } else {
                                    "Owner Mode turned off."
                                }
                            )
                        }
                    },
                    onQuickNavHome = { jumpToHomeSection(HomeScrollTarget.HERO) },
                    onQuickNavProducts = {
                        viewModel.selectCategory(null)
                        navigateToTab(Routes.PRODUCTS)
                    },
                    onQuickNavCategories = { jumpToHomeSection(HomeScrollTarget.CATEGORIES) },
                    onQuickNavAbout = { jumpToHomeSection(HomeScrollTarget.ABOUT) },
                    onQuickNavContact = { navigateToTab(Routes.CONTACT) }
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        APP_TABS.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        APP_TABS.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.HOME
                    ) {
                        composable(Routes.HOME) {
                            HomeScreen(
                                uiState = uiState,
                                scrollTargetRequest = scrollRequestTarget,
                                onExploreProducts = {
                                    viewModel.selectCategory(null)
                                    navigateToTab(Routes.PRODUCTS)
                                },
                                onExploreCategory = { category ->
                                    viewModel.selectCategory(category)
                                    navigateToTab(Routes.PRODUCTS)
                                },
                                onNavigateContact = { navigateToTab(Routes.CONTACT) },
                                onNavigateGallery = { navigateToTab(Routes.GALLERY) },
                                onEditProduct = { product ->
                                    editingProduct = product
                                    showProductDialog = true
                                },
                                onDeleteProduct = { product ->
                                    viewModel.deleteProduct(product)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Removed ${product.name}")
                                    }
                                },
                                onAddOffer = {
                                    editingOffer = null
                                    showOfferDialog = true
                                },
                                onEditOffer = { offer ->
                                    editingOffer = offer
                                    showOfferDialog = true
                                },
                                onDeleteOffer = { offer ->
                                    viewModel.deleteOffer(offer)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Removed ${offer.title}")
                                    }
                                }
                            )
                        }

                        composable(Routes.PRODUCTS) {
                            ProductsScreen(
                                uiState = uiState,
                                onSearchQueryChange = viewModel::updateSearchQuery,
                                onSelectCategory = viewModel::selectCategory,
                                onToggleOwnerMode = viewModel::toggleOwnerMode,
                                onAddProductClick = {
                                    editingProduct = null
                                    showProductDialog = true
                                },
                                onEditProductClick = { product ->
                                    editingProduct = product
                                    showProductDialog = true
                                },
                                onDeleteProductClick = { product ->
                                    viewModel.deleteProduct(product)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Removed ${product.name}")
                                    }
                                }
                            )
                        }

                        composable(Routes.GALLERY) {
                            GalleryScreen(
                                uiState = uiState,
                                onSelectGalleryCategory = viewModel::selectGalleryCategory,
                                onAddGalleryPhoto = { title, category, uri ->
                                    viewModel.addGalleryPhoto(title, category, uri)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Added photo to store gallery")
                                    }
                                },
                                onDeleteGalleryPhoto = { photo ->
                                    viewModel.deleteGalleryPhoto(photo)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Removed gallery photo")
                                    }
                                }
                            )
                        }

                        composable(Routes.CONTACT) {
                            ContactScreen(
                                uiState = uiState,
                                onToggleSaveLocation = {
                                    viewModel.toggleSaveLocation { saved ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                if (saved) {
                                                    "Store location saved to your bookmarks."
                                                } else {
                                                    "Store location removed from bookmarks."
                                                }
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showProductDialog) {
        ProductEditorDialog(
            initialProduct = editingProduct,
            defaultCategory = uiState.selectedCategory ?: StoreCategory.ELECTRONICS,
            onDismiss = { showProductDialog = false },
            onSave = { id, name, brand, cat, desc, price, avail, pickedUri, existingUri, featured ->
                viewModel.saveProduct(
                    existingId = id,
                    name = name,
                    brand = brand,
                    category = cat,
                    description = desc,
                    priceText = price,
                    availabilityText = avail,
                    pickedImageUri = pickedUri,
                    existingImageUri = existingUri,
                    isFeatured = featured
                )
                showProductDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (id == 0L) "Added $name to catalogue" else "Updated $name"
                    )
                }
            }
        )
    }

    if (showOfferDialog) {
        OfferEditorDialog(
            initialOffer = editingOffer,
            onDismiss = { showOfferDialog = false },
            onSave = { id, title, desc, tag, validity, highlight ->
                viewModel.saveOffer(
                    existingId = id,
                    title = title,
                    description = desc,
                    categoryTag = tag,
                    validityNote = validity,
                    isStoreHighlight = highlight
                )
                showOfferDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Saved store offer/highlight")
                }
            }
        )
    }
}
