package com.quik.scan.history

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.quik.scan.R
import com.quik.scan.navigation.Screens
import com.quik.scan.ui.theme.monteSB
import com.quik.scan.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun History(viewModel: MainViewModel, navHostController: NavController) {
    val histories by viewModel.completeHistory.collectAsState(initial = listOf())
    val data = viewModel.searchData.value
    println("The data is $data")
    var value by remember { mutableStateOf("") }
    println("The history is $histories")
    val saved by viewModel.getSaved.collectAsState(initial = listOf())
    var date by remember {
        mutableStateOf("")
    }
    var visible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        if (visible) {
            visible = false
        } else {
            navHostController.popBackStack()
        }
        navHostController.popBackStack()
    }

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
                    text = "History",
                    color = Color.Black,
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
                shape = CircleShape, onClick = {
                    navHostController.navigate(Screens.ScanCode.route)
                }, contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = "QR CODE SCANNER",
                )
            }
        }) {
        println(it)
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Spacer(modifier = Modifier.height(20.dp))

            if (histories.isNotEmpty()) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = visible,
                    modifier = Modifier.fillMaxWidth(),
                    enter = slideInHorizontally(initialOffsetX = { width ->
                        width
                    }, animationSpec = tween(durationMillis = 500)) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { width ->
                        width
                    }, animationSpec = tween(durationMillis = 500)) + fadeOut()
                ) {


                    if (data.isNotEmpty()) {
                        LazyColumn {
                            item {
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = { change ->
                                        value = change
                                        coroutineScope.launch {
                                            viewModel.searchQuery(change)
                                                .collectLatest { searchData ->
                                                    viewModel.searchData.value = searchData
                                                }

                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.Search,
                                            contentDescription = "",
                                            modifier = Modifier.size(25.dp),
                                            tint = Color.Black
                                        )
                                    },
                                    placeholder = {
                                        Text(
                                            text = "Search your Barcodes Here",
                                            color = Color.Black
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),

                                    )
                            }
                            items(data) { history ->
                                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                                val dateString = simpleDateFormat.format(history.date)
                                val daySDF = SimpleDateFormat("dd")
                                val day = daySDF.format(history.date)
                                date =
                                    if (day == SimpleDateFormat("dd").format(System.currentTimeMillis())) {
                                        "Today"
                                    } else if ((day.toInt() + 1) == SimpleDateFormat("dd").format(
                                            System.currentTimeMillis()
                                        )
                                            .toInt()
                                    ) {
                                        "Yesterday"
                                    } else {
                                        dateString
                                    }
                                HComponent(
                                    content = history.content,
                                    types = history.type,
                                    date = date,
                                    isSaved = false,
                                    longDate = history.date,
                                    viewModel = viewModel,
                                    name = history.name,
                                    isShared = history.isShared,
                                    sharedFrom = history.sharedFrom
                                ) {
                                    viewModel.link = history.content
                                    viewModel.isSaved = true
                                    navHostController.navigate(Screens.CreateQr.route)
                                }
                            }
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            OutlinedTextField(value = value, onValueChange = { change ->
                                value = change
                                coroutineScope.launch {
                                    viewModel.searchQuery(change).collectLatest { searchData ->
                                        viewModel.searchData.value = searchData
                                    }

                                }
                            }, leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "",
                                    modifier = Modifier.size(25.dp),
                                    tint = Color.Black
                                )
                            }, placeholder = {
                                Text(text = "Search your Barcodes Here", color = Color.Black)
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                            )

                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.emptyimage),
                                    contentDescription = "",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(350.dp)
                                )
                            }
                        }

                    }
                }
                if (!visible) {
                    if (saved.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f)
                        ) {
                            LazyColumn(reverseLayout = true) {
                                items(saved) { history ->
                                    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                                    val dateString = simpleDateFormat.format(history.date)
                                    val daySDF = SimpleDateFormat("dd")
                                    val day = daySDF.format(history.date)
                                    date =
                                        if (day == SimpleDateFormat("dd").format(System.currentTimeMillis())) {
                                            "Today"
                                        } else if ((day.toInt() + 1) == SimpleDateFormat("dd").format(
                                                System.currentTimeMillis()
                                            )
                                                .toInt()
                                        ) {
                                            "Yesterday"
                                        } else {
                                            dateString
                                        }

                                    HComponent(
                                        content = history.content,
                                        types = history.type,
                                        date = date,
                                        isSaved = history.isSaved,
                                        longDate = history.date,
                                        viewModel = viewModel,
                                        name = history.name,
                                        isShared = history.isShared,
                                        sharedFrom = history.sharedFrom
                                    ) {
                                        viewModel.link = history.content
                                        viewModel.isSaved = true
                                        navHostController.navigate(Screens.CreateQr.route)
                                    }
                                }
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 15.dp),
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        Text(
                                            text = "Saved 💖",
                                            color = Color.Black,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(3.dp),
                                            fontFamily = monteSB
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.3f)) {
                        LazyColumn(reverseLayout = true) {
                            items(histories) { history ->
                                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                                val dateString = simpleDateFormat.format(history.date)
                                val daySDF = SimpleDateFormat("dd")
                                val day = daySDF.format(history.date)
                                date =
                                    if (day == SimpleDateFormat("dd").format(System.currentTimeMillis())) {
                                        "Today"
                                    } else if ((day.toInt() + 1) == SimpleDateFormat("dd").format(
                                            System.currentTimeMillis()
                                        )
                                            .toInt()
                                    ) {
                                        "Yesterday"
                                    } else {
                                        dateString
                                    }
                                if (!history.isSaved) {
                                    HComponent(
                                        content = history.content,
                                        types = history.type,
                                        date = date,
                                        isSaved = false,
                                        longDate = history.date,
                                        viewModel = viewModel,
                                        name = history.name,
                                        isShared = history.isShared,
                                        sharedFrom = history.sharedFrom
                                    ) {
                                        viewModel.link = history.content
                                        viewModel.isSaved = true
                                        navHostController.navigate(Screens.CreateQr.route)
                                    }
                                }
                            }
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 15.dp, end = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = visible,
                                        modifier = Modifier.fillMaxWidth(),
                                        enter = slideInHorizontally(initialOffsetX = { width ->
                                            width
                                        }, animationSpec = tween(durationMillis = 500)) + fadeIn()
                                    ) {
                                        OutlinedTextField(value = value, onValueChange = { change ->
                                            value = change
                                            coroutineScope.launch {
                                                viewModel.searchQuery(change)
                                                    .collectLatest { searchData ->
                                                        viewModel.searchData.value = searchData
                                                    }

                                            }
                                        }, leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.Search,
                                                contentDescription = "",
                                                modifier = Modifier.size(25.dp),
                                                tint = Color.Black
                                            )
                                        }, placeholder = {
                                            Text(
                                                text = "Search your Barcodes Here",
                                                color = Color.Black
                                            )
                                        })
                                    }
                                    if (!visible) {
                                        Text(
                                            text = "All Codes ",
                                            color = Color.Black,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(0.dp),
                                            fontFamily = monteSB
                                        )

                                        IconButton(onClick = { visible = !visible }) {
                                            Icon(
                                                imageVector = Icons.Filled.Search,
                                                contentDescription = "",
                                                modifier = Modifier.size(25.dp),
                                                tint = Color.Black
                                            )
                                        }

                                    }
                                }

                            }
                        }
                    }
//                    HistorySearchScreen(visible = visible)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.nothing),
                        contentDescription = "",
                        modifier = Modifier.size(300.dp)
                    )
                }
            }
        }
    }
}










