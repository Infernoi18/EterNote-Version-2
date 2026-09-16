package com.example.eternotev2.ui.components.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Mood.entries.forEach { mood ->
            val isSelected = mood == selectedMood
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        HapticUtil.performVirtualKey(view)
                        onMoodSelected(mood)
                    }
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.2f)
                        else Color.White.copy(alpha = 0.1f)
                    )
                    .padding(16.dp)
            ) {
                Text(text = mood.emoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mood.label,
                    fontSize = 12.sp,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
