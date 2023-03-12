package com.quik.scan.navigation

sealed class Screens(val route: String) {
    object ScanCode: Screens("qrCodeScanner")
    object CreateQr: Screens("generateQrCode")
    object History: Screens("homePage")
    object SharedQr: Screens("sharedQr")
    object Login: Screens("login")
    object Splash: Screens("splash")
}