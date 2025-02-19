package com.example.shop_app_project.Home_page.Main.SharedPreferencesManager

import android.content.Context
import android.content.SharedPreferences
import com.example.shop_app_project.data.models.product.ProductModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferencesManager {
    private const val PREFS_NAME = "saved_products_prefs"
    private const val SAVED_PRODUCTS_KEY = "saved_products"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Save list of saved products to SharedPreferences
    fun saveSavedProducts(
        context: Context,
        savedProducts: List<ProductModel>
    ) {
        val json = Gson().toJson(savedProducts)
        getPreferences(context).edit().putString(SAVED_PRODUCTS_KEY, json).apply()
    }

    // Load saved products from SharedPreferences
    fun loadSavedProducts(context: Context): List<ProductModel> {
        val json = getPreferences(context).getString(SAVED_PRODUCTS_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<ProductModel>>() {}.type
        return Gson().fromJson(json, type)
    }

    // Clear saved products from SharedPreferences
    fun clearSavedProducts(context: Context) {
        getPreferences(context).edit().remove(SAVED_PRODUCTS_KEY).apply()
    }
}
