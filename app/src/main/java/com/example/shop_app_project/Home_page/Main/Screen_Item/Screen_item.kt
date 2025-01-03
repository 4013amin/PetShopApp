package com.example.shop_app_project.Home_page.Main.Screen_Item

import FavoritesViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.shop_app_project.Home_page.Main.ProductItem
import com.example.shop_app_project.R
import com.example.shop_app_project.data.models.product.ProductModel
import com.example.shop_app_project.data.view_model.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(product: ProductModel) {
    val images = product.image
    Scaffold(

    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Image display
            AsyncImage(
                model = "http://192.168.1.110:2020/${images}",
                contentDescription = "Product image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp)),
                onError = {
                    Log.e("ProductItem", "Error loading image: ${it.result.throwable?.message}")
                },
                onSuccess = {
                    Log.d("ProductItem", "Image loaded successfully")
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
            ) {

                item { InfoRow(value = product.name) }
                item { InfoRow(value = product.nameUser) }
                item { InfoRow(value = product.city) }
                item { InfoRow(value = product.family) }
                item { InfoRow(value = "${product.price} تومان", color = Color.Green) }
                item { InfoRow(value = product.address) }
                item { InfoRow(value = product.description) }
                item { InfoRow(value = product.phone) }
                item { InfoRow(value = product.created_at) }
                item { InfoRow(value = product.updated_at) }
            }
        }
    }
}

@Composable
fun InfoRow(value: String, color: Color = Color.Black) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}



@Preview
@Composable
private fun show() {
    ProductDetailScreen(
        product = ProductModel(
            name = "Example Product",
            image = "example.jpg",
            nameUser = "John Doe",
            city = "Tehran",
            family = "Smith",
            price = 20000,
            address = "123 Street",
            description = "Sample description",
            phone = "1234567890",
            created_at = "2025-01-01",
            updated_at = "2025-01-02",
            id = 20
        )
    )
}


@Composable
fun ImageSlider(images: List<String>) {
    val pagerState = com.google.accompanist.pager.rememberPagerState()

    Column {
        com.google.accompanist.pager.HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier.height(200.dp)
        ) { page ->
            // Use rememberImagePainter to load the image from the URL string
            Image(
                painter = rememberImagePainter(images[page]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(16f / 9f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pager Indicator
        PagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        )
    }
}


@Composable
fun PagerIndicator(
    pagerState: com.google.accompanist.pager.PagerState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { index ->
            val color = if (pagerState.currentPage == index) Color.Black else Color.Gray
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, shape = RoundedCornerShape(50))
                    .padding(2.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductForm(navController: NavController) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel()
    val provinces = mapOf(
        "تهران" to listOf("تهران", "ری", "شمیرانات"),
        "اصفهان" to listOf("اصفهان", "کاشان", "خمینی‌شهر"),
        "خراسان رضوی" to listOf("مشهد", "نیشابور", "تربت حیدریه")
    )

    var selectedProvince by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("") }
    val cities = provinces[selectedProvince] ?: emptyList()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.rectangle),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 150.dp)
                .padding(bottom = 80.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.End
                ) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("نام محصول", textAlign = TextAlign.End) },
                        placeholder = { Text("نام محصول را وارد کنید", textAlign = TextAlign.End) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            textAlign = TextAlign.End
                        )
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("نام محصول", textAlign = TextAlign.End) },
                        placeholder = { Text("تلفن  خود را وارد کنید", textAlign = TextAlign.End) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            textAlign = TextAlign.End
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("توضیحات محصول", textAlign = TextAlign.End) },
                        placeholder = {
                            Text(
                                "توضیحات محصول را وارد کنید",
                                textAlign = TextAlign.End
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            textAlign = TextAlign.End
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("قیمت محصول", textAlign = TextAlign.End) },
                        placeholder = {
                            Text(
                                "قیمت محصول را وارد کنید",
                                textAlign = TextAlign.End
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            textAlign = TextAlign.End
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    AddProductImages(context = context, onImagesSelected = { selectedImages ->
                        images = selectedImages
                    })
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Button(onClick = {
                    if (name.isNotEmpty() && description.isNotEmpty() && price.isNotEmpty() && phone.isNotEmpty() && images.isNotEmpty()) {
                        userViewModel.sendProduct(
                            name = name,
                            description = description,
                            price = price,
                            phone = phone,
                            nameUser = name,
                            imageFile = images.first(),
                            context = context
                        )

                    } else {
                        Toast.makeText(context, "لطفاً تمام فیلدها را پر کنید", Toast.LENGTH_LONG)
                            .show()
                    }
                }) {
                    Text("ذخیره محصول")
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductImages(context: Context, onImagesSelected: (List<Uri>) -> Unit) {
    // لیستی برای ذخیره تصاویر
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris != null && uris.size <= 10) {
            images = uris
            onImagesSelected(uris) // Pass the selected images back to the parent composable
        } else {
            Toast.makeText(
                context,
                "حداکثر 10 تصویر می‌توانید انتخاب کنید.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(images) { image ->
                Image(
                    painter = rememberImagePainter(image),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { imagePicker.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("انتخاب تصاویر")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("تعداد تصاویر انتخابی: ${images.size}/10")
    }
}


@Composable
fun FavoritesScreen(navController: NavController, favoritesViewModel: FavoritesViewModel) {
    LazyColumn {
        items(favoritesViewModel.favorites) { product ->

            favoritesViewModel.loadFavorites()

            ProductItem(
                name = product.name,
                description = product.description,
                price = product.price,
                image = product.image,
                onClick = {
                    navController.navigate("singleProduct/${product.id}")
                },
                onSaveClick = {
                    favoritesViewModel.removeFavorite(product)
                }
            )
        }
    }
}
