package com.quik.scan.generate

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.quik.scan.history.BottomBar
import com.quik.scan.history.GenerateLinkBox
import com.quik.scan.navigation.Screens
import com.quik.scan.ui.theme.monteSB
import com.quik.scan.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun Generate(
    viewModel: MainViewModel,
    navHostController: NavHostController,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var currEmoji by remember { mutableStateOf("\uD83D\uDE42") }
    var emoIndex by remember { mutableStateOf(0) }
    var link by remember { mutableStateOf("")}
    val isScanned = viewModel.isSaved
    BackHandler() {
        viewModel.isSaved = false
        navHostController.popBackStack()
    }
    if (isScanned) {
        link = viewModel.link ?: ""
    }
    var gL by remember { mutableStateOf("")}
    val eList by remember { mutableStateOf(EMOJIS.emojisList) }
    BottomSheetScaffold(
        sheetContent = {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(50.dp),
                modifier = Modifier.fillMaxHeight(0.5f)
            ) {
                itemsIndexed(eList) { index, emoji ->
                    AndroidView(
                        factory = { context ->
                            AppCompatTextView(context).apply {
                                setTextColor(Black.toArgb())
                                text = emoji
                                textSize = 35.0F
                                textAlignment = View.TEXT_ALIGNMENT_CENTER
                                alpha = 1f
                            }
                        },
                        update = { update ->
                            update.apply {
                                text = emoji
                            }
                        },
                        modifier = Modifier.clickable {
                            emoIndex = index
                            currEmoji = emoji
                            coroutineScope.launch {
                                scaffoldState.bottomSheetState.collapse()
                            }
                        }
                    )

                }
            }
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topEnd = 7.dp, topStart = 7.dp),

        ) {
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
                        .clickable {
                            coroutineScope.launch {
                                if (scaffoldState.bottomSheetState.isExpanded) {
                                    scaffoldState.bottomSheetState.collapse()
                                } else {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        },
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
                                        text = currEmoji
                                        textSize = 48.0F
                                        textAlignment = View.TEXT_ALIGNMENT_CENTER
                                        rotation = item.rotation
                                        alpha = 1f
                                    }
                                },
                                update = { update ->
                                    update.apply {
                                        text = currEmoji
                                    }
                                }
                            )
                        }
                    }

                }

                if (link != "") {
                    QrGenerator(
                        link = link,
                        currentEmoji = emoIndex.toString(),
                        navController = navHostController
                    )
                } else  {
                    GenerateLinkBox(value = gL, onValueChange = {value ->
                        gL = value
                    }){
                        link = gL
                    }
                }
            }
        }
    }
}