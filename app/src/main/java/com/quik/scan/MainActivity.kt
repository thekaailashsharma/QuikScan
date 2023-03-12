package com.quik.scan

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.quik.scan.navigation.NavigationController
import com.quik.scan.ui.theme.QuikScanTheme
import com.quik.scan.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fun hideSystemUI() {
            actionBar?.hide()
        }
        setContent {
            QuikScanTheme {

                var dl by remember{ mutableStateOf("") }
                var deepLink by remember{ mutableStateOf<Uri?>(null) }
                Firebase.dynamicLinks
                    .getDynamicLink(intent)
                    .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.link
                            dl = pendingDynamicLinkData.link.toString()

                        }
                    }
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(Color.White)
                systemUiController.setStatusBarColor(Color.White)
                val mainViewModel: MainViewModel = hiltViewModel()
                NavigationController(mainViewModel = mainViewModel, deepLink = dl)
            }
        }
        hideSystemUI()
    }
}