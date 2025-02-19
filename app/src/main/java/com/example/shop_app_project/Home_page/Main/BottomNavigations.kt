import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shop_app_project.Home_page.Main.Screen_Item.AddProductForm
import com.example.shop_app_project.Home_page.Main.Screen_Item.EditProfileScreen
import com.example.shop_app_project.Home_page.Main.Screen_Item.FavoritesPage
import com.example.shop_app_project.Home_page.Main.Screen_Item.LoginUsers.addCodeScreen
import com.example.shop_app_project.Home_page.Main.Screen_Item.LoginUsers.forgetpasswordScreen
import com.example.shop_app_project.Home_page.Main.Screen_Item.ProductDetailScreen
import com.example.shop_app_project.Home_page.Main.Screen_Item.UserProductsScreen
import com.example.shop_app_project.Home_page.Main.Screen_Item.UserProfileDetailScreen

import com.example.shop_app_project.Home_page.Main.UiHomePage
import com.example.shop_app_project.data.models.Profile.UserProfile
import com.example.shop_app_project.data.models.product.ProductModel
import com.example.shop_app_project.data.view_model.SavedProductsViewModel
import com.example.shop_app_project.data.view_model.ShoppingCartViewModel
import com.example.shop_app_project.data.view_model.UserViewModel

data class NavigationsItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
)

val navItems = listOf(
    NavigationsItem("profile", "پروفایل", Icons.Default.Person),
    NavigationsItem("favorites", "نشان ها", Icons.Default.FavoriteBorder),
    NavigationsItem("addProduct", "ثبت آگهی", Icons.Default.Add),
    NavigationsItem("home", "خانه", Icons.Default.Home),
)

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
            ) {
                navItems.reversed().forEach { item ->
                    val isSelected = currentDestination?.route == item.route
                    val scale =
                        animateFloatAsState(targetValue = if (isSelected) 1.2f else 1f).value

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier
                                    .size(20.dp)
                                    .graphicsLayer(scaleX = scale, scaleY = scale),
                                tint = if (isSelected) Color(0xFF007BFF) else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.Black else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavigations(
    navController: NavHostController,
    userViewModel: UserViewModel,
    shoppingCartViewModel: ShoppingCartViewModel,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val isBottomBarVisible = remember { mutableStateOf(true) }
    val scrollState = rememberLazyListState()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0) {
                    isBottomBarVisible.value = true
                } else if (available.y < 0) {
                    isBottomBarVisible.value = false
                }
                return Offset.Zero
            }
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible.value && currentRoute != "forgetPassword" && currentRoute?.startsWith(
                    "addCodeScreen"
                ) != true
            ) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(nestedScrollConnection)
        ) {
            NavGraph(
                navController = navController,
                shoppingCartViewModel = shoppingCartViewModel,
                modifier = Modifier,
                userViewModel
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState", "NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    shoppingCartViewModel: ShoppingCartViewModel,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val userPreferences = remember {
        UserPreferences.getInstance(context)
    }

    val isUserLoggedIn = remember {
        mutableStateOf(userPreferences.isUserLoggedIn())
    }

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn.value) "home" else "forgetPassword",
        modifier = modifier
    ) {
        composable("home") {
            UiHomePage(navController = navController, userViewModel = userViewModel)
        }

        composable("singleProduct/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            if (productId != null) {
                val context = LocalContext.current
                val product = remember { mutableStateOf<ProductModel?>(null) }

                LaunchedEffect(productId) {
                    product.value = userViewModel.getProductById(context, productId)
                }

                product.value?.let { productDetails ->
                    ProductDetailScreen(product = productDetails, onBackClick = {})
                } ?: Text("Loading...")
            } else {
                Text("Invalid product ID")
            }
        }


        composable("favorites") {
            val favoritesViewModel: SavedProductsViewModel = viewModel()
            FavoritesPage(favoritesViewModel, navController)
        }


        composable("forgetPassword") {
            forgetpasswordScreen(navController)
        }


        composable("addCodeScreen?phone={phone}") { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone")
            addCodeScreen(navController, userViewModel, phone)
        }


        composable("addProduct") {
            AddProductForm(navController)
        }

        composable("profile") {
            val userPreferences = UserPreferences.getInstance(context)
            val phone = userPreferences.getUserPhone()
            UserProductsScreen(userViewModel, phone.toString(), context, navController)
        }

        composable("profileDetail") {
            UserProfileDetailScreen(userViewModel, navController)
        }

        composable("EditProfileScreen") {
            EditProfileScreen(userViewModel, navController, context)
        }
    }
}


@Preview
@Composable
private fun showBottomNavigation() {
    val navController = rememberNavController()
    BottomNavigationBar(navController = navController)
}