package com.quik.scan.google

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.quik.scan.R
import com.quik.scan.navigation.Screens
import com.quik.scan.ui.theme.monteSB

@Composable
fun LoginScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val gso =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail().requestProfile()
            .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    LaunchedEffect(key1 = user) {
        if (user != null) {
            navHostController.popBackStack()
            navHostController.navigate(Screens.History.route)
        }
    }
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            user = result.user
        },
        onAuthError = {
            user = null
        }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                painter = painterResource(id = R.drawable.login),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(350.dp)
                    .padding(bottom = 50.dp)
            )
        }

        Text(
            text = "Welcome to QuikScan 💖",
            color = Color(0xFF233483),
            fontFamily = monteSB,
            fontSize = 25.sp,
            modifier = Modifier.padding(start = 10.dp, bottom = 25.dp)
        )

        RepeatedLogin {
            launcher.launch(googleSignInClient.signInIntent)
        }


    }
}

@Composable
fun RepeatedLogin(onClick: () -> Unit) {
    Text(
        text = "Login",
        color = Color(0xFF233483),
        fontFamily = monteSB,
        fontSize = 35.sp,
        modifier = Modifier.padding(start = 10.dp, bottom = 15.dp)
    )

    Text(
        text = "Login to Proceed ahead with our Application",
        color = Color(0xFF233483),
        fontFamily = monteSB,
        fontSize = 15.sp,
        modifier = Modifier.padding(start = 10.dp, bottom = 35.dp)
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0153ff)
            ),
            modifier = Modifier.padding(
                start = 20.dp,
                bottom = 35.dp,
                end = 20.dp
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Continue with Google",
                color = Color.White,
                fontFamily = monteSB,
                fontSize = 20.sp
            )
        }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Text(
            text = "We Protect Your Privacy",
            color = Color(0xFF233483),
            fontFamily = monteSB,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 10.dp, bottom = 25.dp)
        )
    }
}

