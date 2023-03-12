package com.quik.scan.navigation

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.quik.scan.generate.Generate
import com.quik.scan.google.LoginScreen
import com.quik.scan.history.History
import com.quik.scan.qrcode.ui.ScanQr
import com.quik.scan.sharedqr.SharedQr
import com.quik.scan.viewmodel.MainViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun NavigationController(
    mainViewModel: MainViewModel,
    deepLink: String
) {
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController,
        startDestination = Screens.Splash.route
    ) {
        composable(
            route = Screens.History.route,
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(300)
                ) +
                        fadeOut(animationSpec = tween(durationMillis = 300))
            },
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(300)
                ) +
                        fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            History(viewModel = mainViewModel, navHostController = navController)

        }
        composable(
            route = Screens.ScanCode.route,
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(300)
                ) +
                        fadeOut(animationSpec = tween(durationMillis = 300))
            },
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(300)
                ) +
                        fadeOut(animationSpec = tween(durationMillis = 300))
            },
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "quikscan.page.link/fromTile"
                    action = Intent.ACTION_VIEW
                }
            )
        ) {
            ScanQr(viewModel = mainViewModel)
        }
        composable(route = Screens.CreateQr.route) {
            Generate(
                viewModel = mainViewModel,
                navHostController = navController
            )
        }
        composable(
            Screens.SharedQr.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "quikscan.page.link"
                    action = Intent.ACTION_VIEW
                }
            )
        ) {
            SharedQr(
                link = deepLink,
                list = mainViewModel.list,
                navHostController = navController,
                viewModel = mainViewModel
            )
        }
        composable(
            Screens.Login.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "quikscan.page.link/loginRedirect"
                    action = Intent.ACTION_VIEW
                }
            )
        ) {
            LoginScreen(navHostController = navController)

        }
        composable(Screens.Splash.route) {
            SplashScreen(navController = navController)
        }
    }
}


