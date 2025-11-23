package com.example.carebuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carebuddy.viewmodel.FoodViewModel

@Composable
fun FoodSummaryCard(
    vm: FoodViewModel = hiltViewModel(),
    onOpenPicker: () -> Unit
) {
    val entries by vm.todayEntries.collectAsState(initial = emptyList())
    val totalCalories by vm.todayTotal.collectAsState(initial = 0)

    val totalCarbs   = entries.sumOf { it.carbs }
    val totalProtein = entries.sumOf { it.protein }
    val totalFat     = entries.sumOf { it.fat }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text("Today’s intake", style = MaterialTheme.typography.titleMedium)
                    Text("Calories — $totalCalories kcal")
                }
                TextButton(onClick = onOpenPicker) {
                    Text("Add food")
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroChip(label = "Carbs", value = totalCarbs)
                MacroChip(label = "Protein", value = totalProtein)
                MacroChip(label = "Fat", value = totalFat)
            }

            Spacer(Modifier.height(4.dp))

            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text(
                    "${entries.size} items logged",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun MacroChip(label: String, value: Double) {
    AssistChip(
        onClick = {},
        label = {
            Text("$label ${"%.1f".format(value)} g")
        }
    )
}




