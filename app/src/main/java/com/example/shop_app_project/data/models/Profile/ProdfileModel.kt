package com.example.shop_app_project.data.models.Profile

data class ProdfileModel(
    val phone: String,
    val id : Int,
    val password: String
)


data class UserProfile(
    val id: Int,
    val name: String,
    val image: String,
    val credit: Int
)
