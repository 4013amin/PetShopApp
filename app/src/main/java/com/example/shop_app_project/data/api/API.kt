package com.example.shop_app_project.data.api

import com.example.shop_app_project.data.models.Profile.ProdfileModel
import com.example.shop_app_project.data.models.Profile.UserProfile
import com.example.shop_app_project.data.models.product.Category
import com.example.shop_app_project.data.models.product.ProductModel
import com.example.shop_app_project.data.view_model.OPT_Model
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface API {

    @GET("getallProduct/")
    suspend fun getAllProducts(): Response<List<ProductModel>>


    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: Int): Response<ProductModel>


    @Multipart
    @POST("AddProduct/")
    suspend fun addProduct(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("nameUser") nameUser: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("price") price: RequestBody,
        @Part("city") city: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<ProductModel>


    @FormUrlEncoded
    @POST("registerUser/")
    suspend fun registerUser(
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Response<ProdfileModel>


    @GET("GetCategories")
    suspend fun getCategories(): Response<List<Category>>


    @FormUrlEncoded
    @POST("send-otp/")
    suspend fun sendOtp(
        @Field("phone") phone: String
    ): Response<OPT_Model>


    @FormUrlEncoded
    @POST("verify-otp/")
    suspend fun verifyOtp(
        @Field("phone") phone: String,
        @Field("otp") otp: String
    ): Response<OPT_Model>


    @GET("user-products/")
    suspend fun getUserProducts(@Query("phone") phone: String): Response<List<ProductModel>>


    @DELETE("user-products/")
    suspend fun deleteUserProducts(
        @Query("phone") phone: String,
        @Query("id") id: Int
    ): Response<ResponseBody>


    @GET("/profile/")
    suspend fun getProfile(@Query("phone") phone: String): Response<UserProfile>


    @Multipart
    @PUT("profile/")
    suspend fun editProfile(
        @Query("phone") phone: String,
        @Part("name") name: RequestBody,
        @Part("credit") credit: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<UserProfile>


}





