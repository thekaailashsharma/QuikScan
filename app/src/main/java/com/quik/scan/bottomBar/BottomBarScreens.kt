package com.quik.scan.bottomBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.ui.graphics.vector.ImageVector
import com.quik.scan.navigation.Screens

sealed class BottomBarScreens(val route: String?, val title: String?, val icon: ImageVector?) {
    object HistoryScreen : BottomBarScreens(Screens.History.route, "History", Icons.Filled.History)
    object GenerateScreen : BottomBarScreens(Screens.CreateQr.route, "Generate", Icons.Filled.QrCode)
    object ScannerScreen : BottomBarScreens(Screens.ScanCode.route, null, null)
}
val items = listOf(
    BottomBarScreens.HistoryScreen,
    BottomBarScreens.GenerateScreen
)