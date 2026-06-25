package com.example.eternotev2.ui.components.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eternotev2.ui.theme.Mood
import com.example.eternotev2.util.HapticUtil

@Composable
fun MoodSelector(
    selectedMood: Mood?,
    onMoodSelected: (Mood) -> Unit
) {
    val view = LocalView.current
    val lazyListState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
    
    LazyRow(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .graphicsLayer(), // Isolate the entire row for smooth horizontal movement
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        flingBehavior = snapBehavior
    ) {
        items(Mood.entries) { mood ->
            val isSelected = mood == selectedMood
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .graphicsLayer { // Individual hardware layer for each mood card
                        scaleX = if (isSelected) 1.05f else 1f
                        scaleY = if (isSelected) 1.05f else 1f
                        alpha = if (isSelected) 1f else 0.8f
                    }
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        HapticUtil.performVirtualKey(view)
                        onMoodSelected(mood)
                    }
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.15f)
                        else Color.White.copy(alpha = 0.05f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.White.copy(alpha = 0.3f) else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp)
                    .width(60.dp)
            ) {
                Text(text = mood.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mood.label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}
