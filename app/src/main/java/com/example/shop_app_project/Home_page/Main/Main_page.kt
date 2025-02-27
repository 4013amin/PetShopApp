package com.example.shop_app_project.Home_page.Main

import BottomNavigations
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.shop_app_project.Home_page.internet.ConnectivityObserver
import com.example.shop_app_project.R
import com.example.shop_app_project.data.models.Profile.UserProfile
import com.example.shop_app_project.data.view_model.SavedProductsViewModel
import com.example.shop_app_project.data.view_model.ShoppingCartViewModel
import com.example.shop_app_project.data.view_model.UserViewModel
import com.example.shop_app_project.ui.theme.Shop_App_projectTheme
import com.google.gson.Gson
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

val gson = Gson()

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        installSplashScreen()
        val connectivityObserver = ConnectivityObserver(this)
        setContent {
            Shop_App_projectTheme {
                val navController = rememberNavController()
                val userViewModel: UserViewModel = viewModel()
                val shoppingCartViewModel: ShoppingCartViewModel = viewModel()
                val isConnected = connectivityObserver.isConnected
                val userProfile by userViewModel.userProfile

                UiHomePage(navController = navController, userViewModel = userViewModel)

                BottomNavigations(
                    navController,
                    userViewModel,
                    shoppingCartViewModel,
                    userProfile
                )

                if (!isConnected) {
                    InternetErrorMessage()
                }

            }
        }
    }
}


@Composable
fun InternetErrorMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .padding(16.dp)
    ) {
        Text(
            text = "اتصال اینترنت شما قطع شده است.",
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiHomePage(
    userViewModel: UserViewModel,
    navController: NavController,
) {
    val products by userViewModel.products
    val category by userViewModel.category
    val context = LocalContext.current
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val favoriteModel: SavedProductsViewModel = viewModel()

    val filteredProducts = if (searchQuery.isNotEmpty()) {
        products.filter { product ->
            product.name.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true)
        }
    } else {
        products
    }

    LaunchedEffect(Unit) {
        userViewModel.getAllProducts()

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(
                                imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (isSearchActive) "بستن جستجو" else "جستجو",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (isSearchActive) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("جستجو محصول...") },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedPlaceholderColor = Color.Gray,
                                    unfocusedPlaceholderColor = Color.Gray
                                )
                            )
                        } else {
                            Text(
                                text = "محصولات",
                                color = Color.White,
                                modifier = Modifier.padding(15.dp),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.blueM)
                )
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),

        ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(filteredProducts) { product ->
                if (filteredProducts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "محصولی با این مشخصات یافت نشد!"
                            } else {
                                "هنوز محصولی اضافه نشده است، شما اولین نفر باشید!"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {

                    ProductItem(
                        name = product.name,
                        description = product.description,
                        price = product.price,
                        images = product.images,
                        onClick = {
                            navController.navigate("singleProduct/${product.id}")
                        },
                        onSaveClick = {
                            favoriteModel.saveProduct(product)
                            Toast.makeText(
                                context,
                                "محصول شما در علاقه مندی ها ذخیره شد",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                }
            }
        }
    }
}

@Composable
fun ImageSlider(images: List<Int>) {
    val pagerState = rememberPagerState()

    Column {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier.height(200.dp)
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(16f / 9f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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
    pagerState: PagerState,
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


@Composable
fun CategoryItem(created_at: String, name: String) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .clickable { /* Handle click event */ }
            .padding(16.dp)
            .width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = created_at,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProductItem(
    name: String,
    description: String,
    price: Int,
    images: List<String>,
    onClick: () -> Unit,
    onSaveClick: () -> Unit
) {

    val formatter = NumberFormat.getInstance(Locale.US).apply {
        isGroupingUsed = true
    }
    val formattedPrice = formatter.format(price)

    var Is_Favorite by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
            .background(color = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (images.isNotEmpty()) {
                AsyncImage(
                    model = "https://petshopdjango.liara.run/${images.first()}",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    onError = {
                        Log.e("ProductItem", "Error loading image: ${it.result.throwable?.message}")
                    },
                    onSuccess = {
                        Log.d("ProductItem", "Image loaded successfully")
                    }
                )
            } else {

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_pets_24),
                        contentDescription = "No Image",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$formattedPrice تومان",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            IconButton(
                onClick = {
                    Is_Favorite = !Is_Favorite
                    onSaveClick()
                },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (Is_Favorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
                    ),
                    contentDescription = "Save",
                    tint = if (Is_Favorite) Color.Red else Color.Gray // تغییر رنگ آیکن بر اساس وضعیت
                )
            }
        }
    }


    @Composable
    fun AnimalBox(imageRes: Int, backgroundColor: Color, text: String) {
        Box(
            modifier = Modifier
                .size(width = 150.dp, height = 100.dp)
                .background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = text,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
//    @Composable
//    fun AnimalBoxes() {
//        LazyRow(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            item {
//                AnimalBox(
//                    imageRes = R.drawable.dog,
//                    backgroundColor = Color(0xFFFFF3E0),
//                    text = "Dog"
//                )
//            }
//            item {
//                AnimalBox(
//                    imageRes = R.drawable.cat,
//                    backgroundColor = Color(0xFFE0F7FA),
//                    text = "Cat"
//                )
//            }
//            item {
//                AnimalBox(
//                    imageRes = R.drawable.parrot,
//                    backgroundColor = Color(0xFFFFF3E0),
//                    text = "Parrot"
//                )
//            }
//            item {
//                AnimalBox(
//                    imageRes = R.drawable.tools3,
//                    backgroundColor = Color(0xFFE0F7FA),
//                    text = "Tools"
//                )
//            }
//        }
//    }
//}