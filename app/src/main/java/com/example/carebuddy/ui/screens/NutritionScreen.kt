package com.example.carebuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carebuddy.data.local.FoodLogEntity
import com.example.carebuddy.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    vm: FoodViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onAddFood: () -> Unit = {}
) {
    val entries by vm.todayEntries.collectAsState(initial = emptyList())
    val totalCalories by vm.todayTotal.collectAsState(initial = 0)

    val totalCarbs   = entries.sumOf { it.carbs }
    val totalProtein = entries.sumOf { it.protein }
    val totalFat     = entries.sumOf { it.fat }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Nutrition",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- Summary card ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Today", style = MaterialTheme.typography.labelMedium)

                    Text(
                        text = "$totalCalories kcal",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroStat("Carbs", totalCarbs)
                        MacroStat("Protein", totalProtein)
                        MacroStat("Fat", totalFat)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        ) {
                            Text(
                                "${entries.size} items logged",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        TextButton(onClick = { vm.clearToday() }) {
                            Text("Reset day")
                        }
                    }
                }
            }

            // --- Header row for list ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Logged foods", style = MaterialTheme.typography.titleMedium)
                Button(onClick = onAddFood) {
                    Text("Add food")
                }
            }

            // --- List of foods ---
            LoggedFoodsList(entries = entries)
        }
    }
}

/**
 * Chhota pill style stat – carbs / protein / fat
 */
@Composable
private fun MacroStat(
    label: String,
    value: Double
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "${"%.1f".format(value)} g",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Simple, clean list for logged foods.
 */
@Composable
private fun LoggedFoodsList(
    entries: List<FoodLogEntity>
) {
    if (entries.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No foods logged yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(entries, key = { it.id }) { item ->
                FoodLogRow(item)
            }
        }
    }
}

@Composable
private fun FoodLogRow(item: FoodLogEntity) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.name, style = MaterialTheme.typography.bodyLarge)

            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                Text(
                    "${item.calories} kcal • " +
                            "C ${"%.1f".format(item.carbs)}g  " +
                            "P ${"%.1f".format(item.protein)}g  " +
                            "F ${"%.1f".format(item.fat)}g",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

