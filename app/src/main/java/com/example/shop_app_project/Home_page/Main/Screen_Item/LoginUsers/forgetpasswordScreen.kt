package com.example.shop_app_project.Home_page.Main.Screen_Item.LoginUsers

import UserPreferences
import android.annotation.SuppressLint
import android.app.Application
import com.example.shop_app_project.R
import android.os.Build
import android.os.CountDownTimer
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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.shop_app_project.data.view_model.UserViewModel


@Composable
fun forgetpasswordScreen(navController: NavController) {

    val userViewModel: UserViewModel = viewModel()
    val context = LocalContext.current
    val userPreferences = remember {
        UserPreferences.getInstance(context)
    }
    var shouldNavigate by remember { mutableStateOf(false) }
    var rulesAccepted by remember { mutableStateOf(false) }
    var showDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        if (userPreferences.isUserLoggedIn()) {
            shouldNavigate = true
            navController.navigate("home") {
                popUpTo("forgetpasswordScreen") { inclusive = true }
            }
        }
    }

    if (!shouldNavigate) {
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



                            Spacer(modifier = Modifier.height(16.dp))


                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 25.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = rulesAccepted,
                                            onCheckedChange = { rulesAccepted = it }
                                        )
                                        Text(
                                            "پذیرفتن قوانین",
                                            modifier = Modifier.clickable { showDialog = true }
                                        )
                                    }

                                    val isPhoneValid = phone.value.length == 11
                                    val isButtonEnabled = isPhoneValid && rulesAccepted

                                    Button(
                                        onClick = {
                                            if (isPhoneValid) {
                                                navController.navigate("addCodeScreen?phone=${phone.value}")
                                                userViewModel.sendOPT(phone.value, context)
                                            }
                                        },
                                        modifier = Modifier
                                            .width(400.dp)
                                            .height(60.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        enabled = isButtonEnabled,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isPhoneValid) colorResource(id = R.color.blueM)
                                            else colorResource(id = R.color.graBTN)
                                        )
                                    ) {
                                        Text(
                                            text = "دریافت کد",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showDialog) {
                Dialog(onDismissRequest = { showDialog = false }) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "قوانین و مقررات",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = """
                             **هدف اپلیکیشن**  
                               این اپلیکیشن یک بستر آنلاین برای ثبت و نمایش آگهی‌های خرید و فروش حیوانات خانگی و لوازم مرتبط با آن‌هاست.

                             **نمایش شماره تماس**  
                               شماره تماس شما در آگهی‌ها به صورت عمومی نمایش داده می‌شود و تمامی کاربران قادر به مشاهده آن خواهند بود.

                             **مسئولیت کاربران**  
                               مسئولیت حفظ حریم خصوصی و امنیت اطلاعات تماس بر عهده خود کاربر است. در صورت عدم تمایل به نمایش شماره تماس، از ثبت آگهی خودداری کنید.

                             **ممنوعیت سوءاستفاده**  
                               هرگونه سوءاستفاده از اطلاعات کاربران، تبلیغات نامناسب، ارسال اطلاعات نادرست یا ایجاد مزاحمت برای سایر کاربران، منجر به تعلیق یا حذف حساب کاربری خواهد شد.

                             **مسئولیت آگهی‌ها**  
                               صحت و اعتبار آگهی‌های ثبت‌شده به عهده‌ی آگهی‌دهنده است. اپلیکیشن هیچ‌گونه مسئولیتی در قبال صحت اطلاعات درج‌شده در آگهی‌ها ندارد.

                             **حذف یا ویرایش آگهی**  
                               مدیریت اپلیکیشن این حق را دارد که در صورت مشاهده‌ی تخلف یا گزارش کاربران، آگهی‌ها را بدون اطلاع قبلی حذف یا ویرایش کند.

                             **تغییر قوانین**  
                               این قوانین ممکن است در هر زمان تغییر کنند. استفاده مستمر از اپلیکیشن به منزله‌ی پذیرش آخرین نسخه‌ی قوانین خواهد بود.
                        """.trimIndent(),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Right,
                                lineHeight = 20.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showDialog = false },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("بستن")
                            }
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun addCodeScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    phone: String?
) {
    val codes = remember { List(5) { mutableStateOf("") } }
    val focusRequesters = remember { List(5) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val phone = navController.currentBackStackEntry?.arguments?.getString("phone") ?: ""
    val userPreferences = remember { UserPreferences.getInstance(context) }

    val isFieldsFilled = codes.all { it.value.length == 1 }

    var timeRemaining by remember { mutableStateOf(120_000L) }
    var formattedTime by remember { mutableStateOf("2:00") }


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
                            if (isFieldsFilled) {
                                userViewModel.verifyOtp(
                                    phone,
                                    codes.joinToString("") { it.value },
                                    context, navController
                                )
                            }
                            userPreferences.saveUser(context, phone)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(45.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFieldsFilled) colorResource(id = R.color.blueM) else Color.Gray
                        ),
                        enabled = isFieldsFilled,

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
                    Box(modifier = Modifier.fillMaxSize()) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,

                            ) {
                            Text(
                                text = "ارسال مجدد کد $formattedTime",
                                textAlign = TextAlign.End,
                                fontSize = 12.sp, modifier = Modifier.clickable {
                                    userViewModel.sendOPT(phone, context)
                                }
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
}
