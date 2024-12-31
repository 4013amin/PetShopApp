package com.example.shop_app_project.Home_page.Main.Screen_Item.LoginUsers

import com.example.shop_app_project.R
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shop_app_project.data.view_model.UserViewModel


@Composable
fun forgetpasswordScreen(navController: NavController, userViewModel: UserViewModel) {

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.rectangle),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource(id = R.drawable.patternloginr),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
        )

        Image(
            painter = painterResource(id = R.drawable.patternloginl),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopStart)
                .offset(x = 0.dp, y = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 150.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.eectangle),
                        contentDescription = null,
                        modifier = Modifier
                            .size(123.dp)
                            .padding(15.dp)
                    )

                    Text(
                        text = "ورود / ثبت نام",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "شماره تماس و رمز عبور خود را وارد کنید",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(15.dp)
                    )

                    Text(
                        text = "شماره تماس خود را وارد کنید",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Column(
                        modifier = Modifier.padding(top = 35.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val phone = remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }
                        var passwordVisible by remember { mutableStateOf(false) }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .padding(start = 16.dp, end = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(text = "موبایل", textAlign = TextAlign.Right)
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        OutlinedTextField(
                            value = phone.value,
                            onValueChange = { phone.value = it },
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .padding(top = 8.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        Button(
                            onClick = {
                                navController.navigate("addCodeScreen?phone=${phone.value}")
                                userViewModel.sendOPT(phone.toString(), context)
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(
                                    id = R.color.blueM
                                )
                            )
                        ) {
                            Text(
                                text = "دریافت کد",
                                color = Color.White,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun addCodeScreen(navController: NavController, userViewModel: UserViewModel, phone: String?) {
    val codes = remember { List(5) { mutableStateOf("") } }
    val focusRequesters = remember { List(5) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val phone = navController.currentBackStackEntry?.arguments?.getString("phone") ?: ""

    Toast.makeText(context, phone, Toast.LENGTH_SHORT).show()

    var timeRemaining by remember { mutableStateOf(120_000L) }
    var formattedTime by remember { mutableStateOf("2:00") }
    var isOtpExpired by remember { mutableStateOf(false) } // Flag to check OTP expiry



    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.rectangle),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource(id = R.drawable.patternloginr),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
        )

        Image(
            painter = painterResource(id = R.drawable.patternloginl),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopStart)
                .offset(x = 0.dp, y = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 150.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.eectangle),
                        contentDescription = null,
                        modifier = Modifier
                            .size(123.dp)
                            .padding(15.dp)
                    )

                    Text(
                        text = "ورود / ثبت نام",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "شماره تماس و رمز عبور خود را وارد کنید",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(15.dp)
                    )

                    Text(
                        text = "کد ارسال شده را وارد کنید",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        codes.forEachIndexed { index, codeState ->
                            OutlinedTextField(
                                value = codeState.value,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                        if (newValue.isNotEmpty()) {
                                            codeState.value = newValue
                                            if (index < codes.size - 1) {
                                                focusRequesters[index + 1].requestFocus()
                                            } else {
                                                focusManager.clearFocus()
                                            }
                                        } else {
                                            codeState.value = newValue
                                            if (index > 0) {
                                                focusRequesters[index - 1].requestFocus()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(56.dp)
                                    .focusRequester(focusRequesters[index]),
                                textStyle = TextStyle(
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                ),
                                singleLine = true,
                                maxLines = 1,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }


                    Button(
                        onClick = {
                            if (isOtpExpired) {
                                // Send a new OTP if the session expired
                                userViewModel.sendOPT(phone, context)
                                isOtpExpired = false // Reset OTP expired flag
                                timeRemaining = 120_000L // Reset the timer
                            } else {
                                // Correctly concatenate the OTP values from MutableState
                                val otp = codes.joinToString("") { it.value }  // Collect the OTP digits as a string
                                Log.d("OTP", "Sending OTP: $otp")  // Log to confirm the OTP value
                                userViewModel.verifyOTP(phone, otp, context)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(45.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blueM))
                    ) {
                        Text(
                            text = "ارسال",
                            color = Color.White,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                    var timeRemaining by remember { mutableStateOf(120_000L) }
                    var formattedTime by remember { mutableStateOf("2:00") }

                    LaunchedEffect(Unit) {
                        object : CountDownTimer(timeRemaining, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                timeRemaining = millisUntilFinished
                                val minutes = (millisUntilFinished / 1000) / 60
                                val seconds = (millisUntilFinished / 1000) % 60
                                formattedTime = String.format("%d:%02d", minutes, seconds)
                            }

                            override fun onFinish() {
                                formattedTime = "ارسال مجدد کد"
                            }
                        }.start()
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ارسال مجدد کد $formattedTime",
                            textAlign = TextAlign.End,
                            fontSize = 12.sp
                        )

                        Text(
                            modifier = Modifier.clickable {
                                navController.navigate("forgetPassword")
                            },
                            text = "تغییر شماره موبایل",
                            textAlign = TextAlign.End,
                            fontSize = 12.sp
                        )
                    }
                }

            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun showForgetPassword() {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel()
    forgetpasswordScreen(navController = navController, userViewModel)
}