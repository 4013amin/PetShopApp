package com.example.shop_app_project.data.view_model

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shop_app_project.data.models.product.Category
import com.example.shop_app_project.data.models.product.ProductModel
import com.example.shop_app_project.data.utils.UtilsRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException

class UserViewModel(application: Application) : AndroidViewModel(application) {
    var registrationResult = mutableStateOf("")
    var login_result = mutableStateOf("")
    var products = mutableStateOf<List<ProductModel>>(arrayListOf())
    var category = mutableStateOf<List<Category>>(arrayListOf())


    //chech_for_login
    var isLoggedIn by mutableStateOf(false)

    private val shoppingCartViewModel = ShoppingCartViewModel(application)

    private val sharedPreferences =
        application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)


    //checkLogin
    fun checkCredentials(): Boolean {
        val savedCredentials = getSavedCredentials()

        return savedCredentials.first.isNotBlank() && savedCredentials.second.isNotBlank()
    }


    fun getAllProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = try {
                UtilsRetrofit.api.getAllProducts()
            } catch (e: IOException) {
                Log.e("UserViewModel", "Network error occurred while fetching products.", e)
                registrationResult.value = "Network error occurred."
                return@launch
            } catch (e: HttpException) {
                Log.e(
                    "UserViewModel",
                    "HTTP error occurred while fetching products: ${e.code()}",
                    e
                )
                registrationResult.value = "HTTP error occurred: ${e.code()}"
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                Log.d("UserViewModel", "Products fetched successfully: ${response.body()}")
                products.value = response.body()!!
            } else {
                Log.e(
                    "UserViewModel",
                    "Failed to fetch products: ${response.errorBody()?.string()}"
                )
            }

//            getCategories()
        }
    }


    suspend fun getProductById(context: Context, productId: Int): ProductModel? {
        return try {
            val response = UtilsRetrofit.api.getProductById(productId)
            if (response.isSuccessful) {
                response.body()
            } else {
                Toast.makeText(context, "Failed to fetch product", Toast.LENGTH_SHORT).show()
                null
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            null
        } catch (e: HttpException) {
            Toast.makeText(context, "Server error: ${e.code()}", Toast.LENGTH_SHORT).show()
            null
        }
    }


    //    send Categories request
    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = try {
                UtilsRetrofit.api.getCategories()
            } catch (e: IOException) {
                Log.e("UserViewModel", "Network error occurred while fetching products.", e)
                return@launch
            } catch (e: HttpException) {
                Log.e(
                    "UserViewModel",
                    "HTTP error occurred while fetching products: ${e.code()}",
                    e

                )
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                category.value = response.body()!!
            }
        }
    }

    //RegisterUser
    fun registerUser(phone: String, password: String) {
        viewModelScope.launch {
            val response = try {
                UtilsRetrofit.api.registerUser(phone, password)
            } catch (e: IOException) {
                Log.e("UserViewModel", "Network error occurred while registering user.", e)
                registrationResult.value = "Network error occurred."
                return@launch
            } catch (e: HttpException) {
                Log.e(
                    "UserViewModel",
                    "HTTP error occurred while registering user: ${e.code()}",
                    e
                )
                registrationResult.value = "HTTP error occurred: ${e.code()}"
                return@launch
            }
        }
    }


    fun encodeImageToBase64(imageUri: Uri, context: Context): String {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun sendProduct(
        imageUri: Uri,
        name: String,
        description: String,
        price: String,
        context: Context
    ) {
        viewModelScope.launch {
            val base64Image = encodeImageToBase64(imageUri, context)
            val response = try {
                UtilsRetrofit.api.sendProduct(base64Image, name, description, price)
            } catch (e: IOException) {
                Log.e("UserViewModel", "Network error occurred while sending product.", e)
                return@launch
            } catch (e: HttpException) {
                Log.e("UserViewModel", "HTTP error occurred while sending product: ${e.code()}", e)
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                registrationResult.value = "this is ok"
            }
        }
    }


    fun saveCredentials(username: String, password: String, phone: String, location: String) {
        with(sharedPreferences.edit()) {
            putString("username", username)
            putString("password", password)
            putString("phone", phone)
            putString("location", location)
            apply()
        }
    }


    fun getSavedCredentials(): Quadruple<String, String, String, String> {
        val username = sharedPreferences.getString("username", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""
        val phone = sharedPreferences.getString("phone", "") ?: ""
        val location = sharedPreferences.getString("location", "") ?: ""
        return Quadruple(username, password, phone, location)
    }

    data class Quadruple<out A, out B, out C, out D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )

//    // تابع برای اضافه کردن به سبد خرید
//    fun addToCart(product: PorductModel) {
//        shoppingCartViewModel.addToCart(product)
//    }

}




