package com.quik.scan.sharedqr


import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.quik.scan.generate.*
import com.quik.scan.history.BottomBar
import com.quik.scan.navigation.Screens
import com.quik.scan.qrcode.analyzer.BarCodeTypes
import com.quik.scan.ui.theme.monteSB
import com.quik.scan.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SharedQr(
    link: String,
    list: MutableList<Component>,
    navHostController: NavHostController,
    viewModel: MainViewModel
) {
    val content = link.substringAfter("conttent=").substringBefore("&emo")
    val emo = link.substringAfter("&emo=").substringBefore("&name")
    println("Emoji is $emo")
    println("Link is $link")
    println("Content is $content")
    val name = link.substringAfter("name=").substringBefore("+")
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        delay(2000)
        isVisible = true
    }
    val context = LocalContext.current

    val onSave = {
        var type = BarCodeTypes.None
        when {
            content.startsWith("http") or content.startsWith("https") -> {
                type = BarCodeTypes.Url
            }
            content.startsWith("wifi") -> {
                type = BarCodeTypes.Wifi
            }
            content.startsWith("sms") -> {
                type = BarCodeTypes.SMS
            }
        }
        viewModel.insertHistory(
            content = content,
            date = System.currentTimeMillis(),
            isScanned = true,
            type = type,
            isShared = true,
            sharedFrom = name
        )
        Toast.makeText(
            context,
            "Saved Successfully",
            Toast.LENGTH_SHORT
        ).show()
        navHostController.popBackStack()
        navHostController.navigate(Screens.History.route)
    }
    val myList = EMOJIS.emojisList
    var myEmoji by remember { mutableStateOf(myList[0]) }

    myEmoji = try {
        println("EMo.nextInt is $emo")
        myList[emo.toInt()]
    } catch (e: Exception) {
        println("Some CHecks failed")
        myList[0]
    }

    val user by remember { mutableStateOf(Firebase.auth.currentUser) }
    Scaffold(topBar = {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Generate",
                    color = Black,
                    fontFamily = monteSB,
                    fontSize = 25.sp
                )
            }
        }
    },
        bottomBar = {
            BottomBar(
                navController = navHostController,
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    navHostController.navigate(Screens.ScanCode.route)
                },
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = "QR CODE SCANNER",
                )
            }
        }
    ) {
        val itemList = (1..36).map {
            Emoji(
                rotation = Random.nextInt(-90, 50).toFloat(),
                padding = Random.nextInt(0, 10).dp
            )
        }
        println(it)
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(3),
                contentPadding = PaddingValues(top = 30.dp, bottom = 30.dp, start = 0.dp),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                itemsIndexed(itemList) { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = if ((index % 6) == 0) 0.dp else {
                                    if ((index % 3) == 0) 60.dp else 30.dp
                                }, end = if ((index % 3) == 0) 0.dp else 5.dp
                            )
                    ) {
                        AndroidView(
                            factory = { context ->
                                AppCompatTextView(context).apply {
                                    setTextColor(Black.toArgb())
                                    text = myEmoji
                                    textSize = 48.0F
                                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                                    rotation = item.rotation
                                    alpha = 1f
                                }
                            },
                            update = { update ->
                                update.apply {
                                    text = myEmoji
                                }
                            }
                        )
                    }
                }
                items(list) { component ->
                    GComponent(
                        painter = painterResource(id = component.painter),
                        text = component.text
                    )
                }
            }
            QrGenerator(
                link = content,
                currentEmoji = myEmoji,
                isShared = true,
                onSave = {
                    onSave.invoke()
                },
                isLoggedIn = user != null,
                navController = navHostController
            )

            AnimatedVisibility(visible = isVisible, enter = slideInVertically() + fadeIn()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(10.dp),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Shared from $name",
                                color = Black,
                                fontFamily = monteSB,
                                fontSize = 15.sp
                            )
                        }

                    }
                }
            }
        }
    }
}


