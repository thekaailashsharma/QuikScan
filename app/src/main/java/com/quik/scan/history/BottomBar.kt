package com.quik.scan.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.quik.scan.bottomBar.items

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        colors = CardDefaults.cardColors(Color.DarkGray),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(7.dp)
    ) {
        BottomNavigation(
            modifier = Modifier
                .padding(12.dp, 0.dp, 12.dp, 0.dp)
                .height(80.dp),
            elevation = 0.dp,
            backgroundColor = Color.DarkGray
        ) {
            items.forEach {
                val isYellow = currentRoute?.hierarchy?.any { nav ->
                    nav.route == it.route
                } == true
                BottomNavigationItem(
                    icon = {
                        it.icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = "",
                                modifier = Modifier.size(35.dp),
                                tint = if (isYellow) Color.Yellow else Color.Gray
                            )
                        }
                    },
                    label = {
                        it.title?.let {
                            Text(
                                text = it,
                                color = if (isYellow) Color.Yellow else Color.Gray
                            )
                        }
                    },
                    selected = isYellow,
                    selectedContentColor = Color.Yellow,
                    unselectedContentColor = Color.White.copy(alpha = 0.4f),
                    onClick = {
                        it.route?.let { it1 ->
                            navController.navigate(it1) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateLinkBox(
    value: String,
    onValueChange: (String) -> Unit,
    onButtonClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        label = {
                            Text(text = "Enter Data for QrCode", color = Color.Black)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedTextColor = Color.Black,
                            containerColor = Color.White
                        )
                    )
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    Button(
                        onClick = onButtonClick, colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0153ff)
                        )
                    ) {
                        Text(text = "Generate", color = Color.White)
                    }
                }
            }
        }
    }
}