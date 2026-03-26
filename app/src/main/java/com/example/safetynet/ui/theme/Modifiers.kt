package com.example.safetynet.ui.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.safeContentPadding(
    statusBar: Boolean = true,
    bottomBar: Boolean = true
): Modifier = this.then(
    if (statusBar) Modifier.statusBarsPadding() else Modifier
).then(
    if (bottomBar) Modifier.padding(bottom = 88.dp) else Modifier
)