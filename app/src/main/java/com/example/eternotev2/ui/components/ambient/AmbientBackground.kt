package com.example.eternotev2.ui.components.ambient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.eternotev2.ui.theme.DeepVoid

@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    primaryColor: Color,
    secondaryColor: Color,
    tertiaryColor: Color = Color.Transparent
) {
    Box(
        modifier = modifier
            .background(DeepVoid)
    ) {
        StarField()
        GlowOrb(
            color = primaryColor,
            modifier = Modifier
        )
        if (tertiaryColor != Color.Transparent) {
            GlowOrb(
                color = tertiaryColor,
                modifier = Modifier,
                sizePx = 300f
            )
        }
    }
}
