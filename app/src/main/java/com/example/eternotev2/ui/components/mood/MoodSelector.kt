package com.example.eternotev2.ui.components.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eternotev2.ui.theme.Mood

@Composable
fun MoodSelector(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Mood.values().forEach { mood ->
            val isSelected = mood == selectedMood
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onMoodSelected(mood) }
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .padding(8.dp)
            ) {
                Text(text = mood.emoji, fontSize = 24.sp)
                Text(
                    text = mood.label,
                    fontSize = 10.sp,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}
