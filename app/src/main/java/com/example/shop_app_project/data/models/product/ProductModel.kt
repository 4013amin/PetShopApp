package com.example.shop_app_project.data.models.product

data class ProductModel(
    val id: Int,
    val images: List<String>,
    val name: String,
    val description: String,
    val nameUser: String,
    val phone: String,
    val city: String,
    val address: String,
    val family: String,
    val image: String?,
    val price: Int,
    val created_at: String,
    val updated_at: String
)

data class ImageModel(
    val image_url: String
)

