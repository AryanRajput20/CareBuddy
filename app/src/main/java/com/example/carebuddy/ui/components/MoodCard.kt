package com.example.carebuddy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carebuddy.viewmodel.MoodViewModel

@Composable
fun MoodCard(
    onOpenMood: (String) -> Unit,
    vm: MoodViewModel = hiltViewModel()
) {
    val last by vm.lastMood.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenMood(last ?: "🙂") },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("How are you feeling?", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Text(last ?: "Tap to share your mood", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // a small emoji preview
            Text(
                text = last?.split(" ")?.firstOrNull() ?: "🙂",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

