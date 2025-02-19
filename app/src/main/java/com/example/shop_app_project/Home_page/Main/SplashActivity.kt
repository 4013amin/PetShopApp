//package com.example.shop_app_project.Home_page.Main
//
//import androidx.compose.ui.draw.alpha
//import com.example.shop_app_project.R
//
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.animation.core.*
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//
//class SplashActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // بعد از 3 ثانیه وارد صفحه اصلی شود
//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }, 3000)
//
//        setContent {
//                SplashScreen()
//
//        }
//    }
//}
//
//@Composable
//fun SplashScreen() {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//
//            // لوگوی اصلی
//            Image(
//                painter = painterResource(id = R.drawable.splash_icon),
//                contentDescription = "App Logo",
//                modifier = Modifier.size(150.dp) // اندازه لوگو
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // نام برنامه
//            Text(
//                text = "Shop App",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color(0xFF1565C0)
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // آیکن لودینگ
//            LoadingIndicator()
//        }
//    }
//}
//
//@Composable
//fun LoadingIndicator() {
//    val infiniteTransition = rememberInfiniteTransition()
//    val alpha by infiniteTransition.animateFloat(
//        initialValue = 0.3f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1000, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        )
//    )
//
//    Image(
//        painter = painterResource(id = R.drawable.loading_icon),
//        contentDescription = "Loading",
//        modifier = Modifier
//            .size(50.dp)
//            .alpha(alpha)
//    )
//}
