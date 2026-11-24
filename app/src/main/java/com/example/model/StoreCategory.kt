package com.example.model

import androidx.annotation.DrawableRes
import com.example.R

enum class StoreCategory(
    val id: String,
    val displayName: String,
    val shortDescription: String,
    val includedItems: List<String>,
    @param:DrawableRes val imageRes: Int
) {
    ELECTRONICS(
        id = "ELECTRONICS",
        displayName = "Electronics & Home Appliances",
        shortDescription = "Televisions, refrigerators, washing machines, air conditioners, fans, smartphones & accessories.",
        includedItems = listOf(
            "Televisions",
            "Refrigerators",
            "Washing Machines",
            "Air Conditioners",
            "Fans",
            "Smartphones",
            "Electronic Accessories"
        ),
        imageRes = R.drawable.img_cat_electronics_1795534493306
    ),
    MILKY_MIST(
        id = "MILKY_MIST",
        displayName = "Milky Mist Dairy Products",
        shortDescription = "Fresh Milky Mist milk, paneer, curd, butter, cheese, ghee & dairy essentials.",
        includedItems = listOf(
            "Milk",
            "Paneer",
            "Curd",
            "Butter",
            "Cheese",
            "Ghee",
            "Other Milky Mist products"
        ),
        imageRes = R.drawable.img_cat_milky_mist_1795534494498
    ),
    GROCERIES(
        id = "GROCERIES",
        displayName = "Groceries & Packaged Foods",
        shortDescription = "Biscuits, snacks, packaged foods, and everyday kitchen grocery staples.",
        includedItems = listOf(
            "Biscuits",
            "Snacks",
            "Packaged Foods",
            "Everyday Grocery Items"
        ),
        imageRes = R.drawable.img_cat_groceries_essentials_1795534495627
    ),
    BEVERAGES(
        id = "BEVERAGES",
        displayName = "Beverages & Cold Drinks",
        shortDescription = "Refreshing soft drinks, fruit juices, bottled drinking water & assorted beverages.",
        includedItems = listOf(
            "Soft Drinks",
            "Juices",
            "Bottled Water",
            "Other Beverages"
        ),
        imageRes = R.drawable.img_cat_groceries_essentials_1795534495627
    ),
    HOUSEHOLD(
        id = "HOUSEHOLD",
        displayName = "Household Essentials",
        shortDescription = "Soaps, detergents, home cleaning supplies, and personal & household care items.",
        includedItems = listOf(
            "Soaps",
            "Detergents",
            "Cleaning Products",
            "Personal and Household Care Items"
        ),
        imageRes = R.drawable.img_cat_groceries_essentials_1795534495627
    ),
    DAILY_ESSENTIALS(
        id = "DAILY_ESSENTIALS",
        displayName = "Daily-Use Products",
        shortDescription = "Everyday products, home necessities, and other convenient store items.",
        includedItems = listOf(
            "Everyday Products",
            "Home Necessities",
            "Other Available Store Products"
        ),
        imageRes = R.drawable.img_hero_showroom_1795534492675
    );

    companion object {
        fun fromId(id: String): StoreCategory =
            entries.firstOrNull { it.id == id } ?: DAILY_ESSENTIALS
    }
}

enum class GalleryCategory(
    val id: String,
    val displayName: String
) {
    STORE_EXTERIOR("STORE_EXTERIOR", "Store Exterior"),
    STORE_INTERIOR("STORE_INTERIOR", "Store Interior"),
    ELECTRONICS("ELECTRONICS", "Electronics"),
    MILKY_MIST("MILKY_MIST", "Milky Mist Products"),
    GROCERY_PRODUCTS("GROCERY_PRODUCTS", "Grocery Products"),
    OTHER_PRODUCTS("OTHER_PRODUCTS", "Other Available Products");

    companion object {
        fun fromId(id: String): GalleryCategory =
            entries.firstOrNull { it.id == id } ?: OTHER_PRODUCTS
    }
}
