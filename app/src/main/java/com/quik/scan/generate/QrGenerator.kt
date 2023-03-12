package com.quik.scan.generate

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.sharp.Link
import androidx.compose.material.icons.sharp.OpenInBrowser
import androidx.compose.material.icons.sharp.SaveAs
import androidx.compose.material.icons.sharp.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.quik.scan.navigation.Screens
import com.quik.scan.ui.theme.monteNormal
import com.simonsickle.compose.barcodes.Barcode
import com.simonsickle.compose.barcodes.BarcodeType

@Composable
fun QrGenerator(
    navController: NavController,
    link: String,
    currentEmoji: String,
    isShared: Boolean = false,
    isLoggedIn: Boolean = true,
    onSave: (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val clipboardManager = LocalClipboardManager.current
        val context = LocalContext.current
        val user by remember { mutableStateOf(Firebase.auth.currentUser) }
        val name = user?.displayName
        val dL = FirebaseDynamicLinks.getInstance().dynamicLink {
            setLink(Uri.parse("https://quikscan.page.link/?conttent=${link}&emo=${currentEmoji}&name=${name}"))
            domainUriPrefix = "https://quikscan.page.link"
            androidParameters(packageName = "com.quik.scan") {
                this.build()
                fallbackUrl = Uri.parse(link)
            }
            socialMetaTagParameters {
                title = "Barcode shared from $name"
                description = "Create Share Enjoy with Ease"
                imageUrl = Uri.parse(
                    "https://firebasestorage.googleapis.com/v0/b/quikscan-" +
                            "ea617.appspot.com/o/appicon.png?alt=media&token=3831e2b5-fec3-4b7e-" +
                            "a7c3-752a15a2833d"
                )
            }
        }
        val dynamicLink = dL.uri.toString()
        Column(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier.padding(start = 30.dp, end = 30.dp, bottom = 7.dp),
                colors = CardDefaults.cardColors(Color.White),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                if (BarcodeType.QR_CODE.isValueValid(dynamicLink)) {
                    Barcode(
                        modifier = Modifier
                            .width(500.dp)
                            .height(300.dp),
                        resolutionFactor = 10,
                        type = BarcodeType.QR_CODE,
                        value = link,
                    )
                } else {
                    Icon(imageVector = Icons.Filled.EmojiPeople, contentDescription = "")
                }
            }

            Card(
                modifier = Modifier.padding(start = 30.dp, end = 30.dp),
                colors = CardDefaults.cardColors(Color.White),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            if (isLoggedIn) {
                                if (isShared) {
                                    onSave?.invoke()
                                } else {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, dynamicLink)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(
                                        sendIntent,
                                        "Share your Barcode",
                                    )
                                    context.startActivity(shareIntent)
                                }
                            } else {
                                navController.popBackStack()
                                navController.navigate(Screens.Login.route)
                                Toast.makeText(
                                    context,
                                    "Login to continue",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        }) {
                            Icon(
                                imageVector = if (isShared) Icons.Sharp.SaveAs else Icons.Sharp.Share,
                                contentDescription = "share",
                                tint = Color.Black,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                        Text(
                            text = if (isShared) "Save Barcode" else "Share Barcode",
                            color = Color.Black,
                            fontFamily = monteNormal,
                            fontSize = 10.sp
                        )
                    }
                    if (link.startsWith("http") or
                        link.startsWith("https") or
                        link.startsWith("mailto") or
                        link.startsWith("upi")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = {
                                val urlIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                context.startActivity(urlIntent)
                            }) {
                                Icon(
                                    imageVector = Icons.Sharp.OpenInBrowser,
                                    contentDescription = "share",
                                    tint = Color.Black,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Text(
                                text = "Open in Browser",
                                color = Color.Black,
                                fontFamily = monteNormal,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString((link)))
                            Toast.makeText(
                                context,
                                "Copied to clipboard Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Icon(
                                imageVector = Icons.Sharp.Link,
                                contentDescription = "share",
                                tint = Color.Black,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Text(
                            text = "Copy Content",
                            color = Color.Black,
                            fontFamily = monteNormal,
                            fontSize = 10.sp
                        )
                    }

                }
            }
        }


    }
}

