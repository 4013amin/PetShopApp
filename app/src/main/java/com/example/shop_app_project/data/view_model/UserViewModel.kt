package com.example.shop_app_project.data.view_model

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.shop_app_project.data.models.Profile.UserProfile
import com.example.shop_app_project.data.models.product.Category
import com.example.shop_app_project.data.models.product.ProductModel
import com.example.shop_app_project.data.utils.UtilsRetrofit
import com.example.shop_app_project.data.utils.UtilsRetrofit.api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException


data class OPT_Model(
    val phone: String,
    val otp: String,
)

sealed class ApiState<out T> {
    object Loading : ApiState<Nothing>()
    data class Success<out T>(val data: T) : ApiState<T>()
    data class Error(val message: String) : ApiState<Nothing>()
}


class UserViewModel(application: Application) : AndroidViewModel(application) {
    var registrationResult = mutableStateOf("")
    var login_result = mutableStateOf("")
    var products = mutableStateOf<List<ProductModel>>(arrayListOf())
    var category = mutableStateOf<List<Category>>(arrayListOf())
    var userProducts = mutableStateOf<List<ProductModel>>(arrayListOf())
    var userProfile = mutableStateOf<UserProfile?>(null)


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
            try {
                val response = withTimeout(60_000) {
                    UtilsRetrofit.api.getAllProducts()
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


//            getCategories()
        }
    }


    suspend fun getProductById(context: Context, productId: Int): ProductModel? {
        return try {
            withTimeout(60_000) {
                val response = UtilsRetrofit.api.getProductById(productId)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Toast.makeText(context, "Failed to fetch product", Toast.LENGTH_SHORT).show()
                    null
                }
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


    //OPT
    fun sendOPT(phone: String, context: Context) {
        viewModelScope.launch {
            val response = try {
                UtilsRetrofit.api.sendOtp(phone)
            } catch (e: IOException) {
                Toast.makeText(context, "this is error in Io", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(context, "This is error in Http Request", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }

        }
    }

    fun verifyOtp(phone: String, otp: String, context: Context, navController: NavController) {
        viewModelScope.launch {
            val response = try {
                UtilsRetrofit.api.verifyOtp(phone, otp)
            } catch (e: IOException) {
                Toast.makeText(context, "Error in network", Toast.LENGTH_SHORT).show()
                return@launch
            } catch (e: HttpException) {
                Toast.makeText(context, "Error in request: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                Toast.makeText(context, "OTP verified successfully", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
            } else {
                // Log the error body or message for better debugging
                Log.e("VerifyOTP", "Error: ${response.errorBody()?.string()}")
                Toast.makeText(
                    context,
                    "Error: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT
                ).show()
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


    fun encodeImageToBase64(context: Context, imageUri: Uri): String {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    var job: Job? = null

    //NetWork
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun sendProduct(
        name: String,
        description: String,
        price: String,
        phone: String,
        nameUser: String,
        city: String,
        imageFiles: List<Uri>,
        context: Context
    ) {
        if (!isNetworkAvailable(context)) {
            Toast.makeText(
                context,
                "اتصال اینترنت برقرار نیست. لطفا وضعیت شبکه خود را بررسی کنید.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        job = viewModelScope.launch(Dispatchers.IO) {
            withTimeout(60_000) {
                try {
                    Log.d("sendProduct", "Preparing to send product data...")

                    val contentResolver = context.contentResolver

                    val imageParts = imageFiles.map { imageFile ->
                        async(Dispatchers.IO) {
                            val resizedBitmap =
                                decodeSampledBitmapFromUri(imageFile, context, 800, 800)
                            val compressedBytes = compressBitmap(resizedBitmap, 70)
                            val imageRequestBody =
                                compressedBytes.toRequestBody("image/webp".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData(
                                "images",
                                "image${imageFiles.indexOf(imageFile)}.webp",
                                imageRequestBody
                            )
                        }
                    }.awaitAll()


                    Log.d("ImageParts", "Image parts count: ${imageParts.size}")

                    // تبدیل داده‌های متنی به RequestBody
                    val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                    val descriptionBody =
                        description.toRequestBody("text/plain".toMediaTypeOrNull())
                    val priceBody = price.toRequestBody("text/plain".toMediaTypeOrNull())
                    val phoneBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
                    val nameUserBody = nameUser.toRequestBody("text/plain".toMediaTypeOrNull())
                    val cityBody = city.toRequestBody("text/plain".toMediaTypeOrNull())

                    Log.d("sendProduct", "Calling API to send product...")

                    withTimeout(120_000) {
                        val response = UtilsRetrofit.api.addProduct(
                            name = nameBody,
                            description = descriptionBody,
                            nameUser = nameUserBody,
                            phone = phoneBody,
                            price = priceBody,
                            city = cityBody,
                            images = imageParts
                        )

                        if (response.isSuccessful) {
                            Log.d("sendProduct", "Product saved successfully.")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "محصول با موفقیت ذخیره شد",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("sendProduct", "Error response: $errorBody")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "خطا در ذخیره محصول: $errorBody",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    Log.e("sendProduct", "Job cancelled: ${e.message}")
                } catch (e: Exception) {
                    Log.e("sendProduct", "Unexpected error occurred", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "خطای ناشناخته رخ داد: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    // تابع فشرده‌سازی تصویر
    @RequiresApi(Build.VERSION_CODES.R)
    private fun compressBitmap(bitmap: Bitmap, quality: Int = 70): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, quality, outputStream)
        return outputStream.toByteArray()
    }

    private fun decodeSampledBitmapFromUri(
        uri: Uri,
        context: Context,
        reqWidth: Int = 800,
        reqHeight: Int = 800
    ): Bitmap {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565 // کاهش مصرف حافظه
        options.inDither = false // سریع‌تر کردن پردازش
        options.inPreferQualityOverSpeed = false // افزایش سرعت دیکودینگ

        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        } ?: throw IllegalArgumentException("Invalid image file: $uri")
    }


    // تابع محاسبه inSampleSize برای تغییر ابعاد تصویر
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }


    fun cancelJob() {
        job?.cancel()
    }


    fun getUserProducts(phone: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {

            withTimeout(60_000) {
                val response = try {
                    UtilsRetrofit.api.getUserProducts(phone)
                } catch (e: IOException) {
                    Log.e(
                        "UserViewModel",
                        "Network error occurred while fetching user products.",
                        e
                    )
                    Toast.makeText(context, "Network error occurred.", Toast.LENGTH_SHORT)
                        .show()
                    return@withTimeout
                } catch (e: HttpException) {
                    Log.e(
                        "UserViewModel",
                        "HTTP error occurred while fetching user products: ${e.code()}",
                        e
                    )
                    when (e.code()) {
                        502 -> {
                            Toast.makeText(
                                context,
                                "Server error (502). Please try again later.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                context,
                                "HTTP error occurred: ${e.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    return@withTimeout
                } catch (e: Exception) {
                    Log.e(
                        "UserViewModel",
                        "Unexpected error occurred while fetching user products.",
                        e
                    )
                    Toast.makeText(context, "Unexpected error occurred.", Toast.LENGTH_SHORT)
                        .show()
                    return@withTimeout
                }
                if (response.isSuccessful && response.body() != null) {
                    userProducts.value = response.body()!!
                } else {
                    Log.e(
                        "UserViewModel",
                        "Failed to fetch user products: ${response.errorBody()?.string()}"
                    )
                    Toast.makeText(
                        context,
                        "Failed to fetch user products.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }


        }
    }

    fun deleteUserProduct(phone: String, id: Int, context: Context) {
        viewModelScope.launch {
            withTimeout(30_000) {
                Log.d("DeleteProduct", "Sending DELETE request: Phone: $phone, Product ID: $id")

                val response = try {
                    UtilsRetrofit.api.deleteUserProducts(phone, id)
                } catch (e: IOException) {
                    Log.e("DeleteProduct", "Network Error: ${e.message}")
                    return@withTimeout
                } catch (e: HttpException) {
                    Log.e("DeleteProduct", "HTTP Error: ${e.message}")
                    return@withTimeout
                }

                if (!response.isSuccessful) {
                    Log.e("DeleteProduct", "Server Response Error: ${response.code()}")
                    Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "با موفیت حذف شد", Toast.LENGTH_SHORT).show()
                    Log.d("DeleteProduct", "Product deleted successfully")
                }
            }
        }
    }


    fun getProfileViewModel(context: Context, phone: String) {
        viewModelScope.launch {
            withTimeout(30_000) {
                val response = try {
                    UtilsRetrofit.api.getProfile(phone)
                } catch (e: IOException) {
                    Toast.makeText(context, "This is error in ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                    return@withTimeout
                }
                if (response.isSuccessful && response.body() != null) {
                    userProfile.value = response.body()!!
                }
            }
        }
    }


    fun editProfileViewModel(context: Context, name: String, image: String, credit: Int) {

        viewModelScope.launch {
            var response = try {
                UtilsRetrofit.api.editProfile(name, image, credit)
            } catch (e: IOException) {
                Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
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


}





