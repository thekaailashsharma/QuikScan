package com.quik.scan.generate

import androidx.compose.ui.unit.Dp

data class Component(
    val painter: Int,
    val text: String
)

data class Emoji(
    val rotation: Float,
    val padding: Dp
)

