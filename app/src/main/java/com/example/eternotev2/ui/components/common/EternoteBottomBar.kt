package com.example.eternotev2.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eternotev2.navigation.BottomNavTab

@Composable
fun EternoteBottomBar(
    currentRoute: String?,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.08f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTab.entries.forEach { tab ->
                val isSelected = currentRoute == tab.route
                
                BottomNavItem(
                    tab = tab,
                    isSelected = isSelected,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    tab: BottomNavTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (tab) {
        BottomNavTab.HOME -> if (isSelected) Icons.Filled.HourglassFull else Icons.Outlined.HourglassEmpty
        BottomNavTab.TIMELINE -> if (isSelected) Icons.Filled.Timeline else Icons.Outlined.Timeline
        BottomNavTab.CORE_MEMORY -> if (isSelected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
        BottomNavTab.INSIGHTS -> if (isSelected) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.label,
            tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = tab.label,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
            fontSize = 10.sp
        )
    }
}
