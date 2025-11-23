package com.example.carebuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carebuddy.viewmodel.HydrationViewModel

@Composable
fun HydrationCard(
    vm: HydrationViewModel = hiltViewModel()
) {
    val total by vm.total.collectAsState()
    val goal = 2000
    val progress = (total.toFloat() / goal.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Hydration", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("$total ml today", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.addWater(100) }) { Text("+100 ml") }
                Button(onClick = { vm.addWater(250) }) { Text("+250 ml") }
                OutlinedButton(onClick = { vm.resetToday() }) { Text("Reset") }
            }
        }
    }
}

