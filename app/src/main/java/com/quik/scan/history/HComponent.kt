package com.quik.scan.history

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quik.scan.qrcode.analyzer.BarCodeTypes
import com.quik.scan.ui.theme.monteNormal
import com.quik.scan.ui.theme.monteSB
import com.quik.scan.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HComponent(
    content: String,
    isSaved: Boolean = false,
    name: String?,
    longDate: Long,
    types: BarCodeTypes,
    date: String,
    viewModel: MainViewModel,
    isShared: Boolean,
    sharedFrom: String?,
    onClick: () -> Unit
) {
    var painter by remember { mutableStateOf<Int?>(null) }
    val keyboard = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var rename by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf(types.name) }
    val onDone = {
        viewModel.updateHistory(
            content = content,
            isSaved = isSaved,
            date = longDate,
            type = types,
            isScanned = true,
            name = value,
            isShared = isShared,
            sharedFrom = sharedFrom
        )
    }
    when (types) {
        BarCodeTypes.CalendarEvent -> {

        }
        BarCodeTypes.None -> {
            if (content.startsWith("upi://")) {
                painter = com.quik.scan.R.drawable.upi
            } else {
                painter = com.quik.scan.R.drawable.none
            }
        }
        BarCodeTypes.DrivingLicense -> {
            painter = com.quik.scan.R.drawable.upi
        }
        BarCodeTypes.Email -> {
            painter = com.quik.scan.R.drawable.email
        }
        BarCodeTypes.GeoPoint -> {
            painter = com.quik.scan.R.drawable.none
        }
        BarCodeTypes.Phone -> {
            painter = com.quik.scan.R.drawable.phone
        }
        BarCodeTypes.SMS -> {
            painter = com.quik.scan.R.drawable.sms
        }
        BarCodeTypes.Url -> {
            if (content.startsWith("https://wa.me")) {
                painter = com.quik.scan.R.drawable.whatsapp
            } else painter = if (content.startsWith("https://linkedin.com")) {
                com.quik.scan.R.drawable.linkedin
            } else if (content.startsWith("https://youtube.com")) {
                com.quik.scan.R.drawable.youtube
            } else if (content.startsWith("https://github.com")) {
                com.quik.scan.R.drawable.github
            } else if (content.startsWith("https://twitter.com")) {
                com.quik.scan.R.drawable.twitter
            } else {
                com.quik.scan.R.drawable.url
            }
        }
        BarCodeTypes.Wifi -> {
            painter = com.quik.scan.R.drawable.wifi
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(11.dp)
            .clickable {
                onClick.invoke()
            },
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(width = 0.dp, color = Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            painter?.let { painterResource(id = it) }?.let {
                Icon(
                    painter = it,
                    contentDescription = content,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(30.dp)
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth(0.75f)
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!rename) {
                        Text(
                            text = name ?: types.name,
                            fontFamily = monteSB,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(end = 7.dp)
                                .clickable {
                                    rename = !rename
                                    keyboard?.show()
                                }
                        )
                    } else {
                        BasicTextField(value = value, onValueChange = {
                            if (it.length < 9) {
                                value = it
                            } else {
                                Toast.makeText(context, "Max 9 Letters Allowed", Toast.LENGTH_SHORT).show()
                            }
                        }, enabled = rename, modifier = Modifier.pointerInput(Unit) {
                            this.detectTapGestures(onDoubleTap = {
                                rename = !rename
                            })
                        }, textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontFamily = monteSB,
                            color = Color.Black
                        ), keyboardActions = KeyboardActions(onAny = {
                            onDone()
                        }),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
                        )
                    }
                    if (!rename) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                            shape = RoundedCornerShape(5.dp),
                        ) {
                            androidx.compose.material.Text(
                                text = date,
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(3.dp)
                            )
                        }
                    }
                    if (isShared && sharedFrom != null && !rename) {
                        OutlinedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            modifier = Modifier.padding(start = 17.dp),
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "",
                                    tint = Color.Black,
                                    modifier = Modifier.size(15.dp)
                                )

                                Text(
                                    text = sharedFrom,
                                    color = Color.Black,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                }
                Text(
                    text = content,
                    fontFamily = monteNormal,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (rename) {
                        onDone()
                        rename = false
                    } else {
                        coroutineScope.launch {
                            viewModel.updateHistory(
                                content = content,
                                date = longDate,
                                type = types,
                                isScanned = true,
                                isSaved = !isSaved,
                                name = name,
                                isShared = isShared,
                                sharedFrom = sharedFrom
                            )
                        }

                    }
                }) {
                    Icon(
                        imageVector = if (rename) Icons.Filled.Check else Icons.Filled.PushPin,
                        contentDescription = "",
                        tint = if (isSaved) Color(0xFF00a884) else Color.Gray,
                        modifier = Modifier.rotate(if (rename) 0f else 50f)
                    )
                }
            }
        }
    }

}
