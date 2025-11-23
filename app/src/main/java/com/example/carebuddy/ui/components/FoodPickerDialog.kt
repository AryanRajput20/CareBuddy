package com.example.carebuddy.ui.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carebuddy.data.remote.NutritionFood
import com.example.carebuddy.viewmodel.FoodViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodPickerDialog(
    foodVm: FoodViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var quantityDialogFood by remember { mutableStateOf<NutritionFood?>(null) }

    val results by foodVm.searchResults.collectAsState()
    val isLoading by foodVm.isSearching.collectAsState()
    val error by foodVm.searchError.collectAsState()

    val scope = rememberCoroutineScope()
    var debounceJob: Job? by remember { mutableStateOf(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            tonalElevation = 8.dp,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text("Search food", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        debounceJob?.cancel()
                        debounceJob = scope.launch {
                            delay(350)
                            val q = query.trim()
                            if (q.isNotEmpty()) {
                                Log.d("FoodPickerDialog", "searchFoods -> $q")
                                foodVm.searchFoods(q)
                            }
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    placeholder = { Text("e.g. banana, paneer, rice") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                when {
                    isLoading -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }

                    !error.isNullOrBlank() -> Text(
                        "Error: $error",
                        color = MaterialTheme.colorScheme.error
                    )

                    results.isEmpty() -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) { Text("No results") }

                    else -> LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp)
                            .animateContentSize()
                    ) {
                        items(results, key = { it.food_name + it.calories }) { nf ->
                            FoodPickerRow(nf = nf) {
                                quantityDialogFood = nf
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Close") }
                }
            }
        }
    }

    // grams selector dialog
    if (quantityDialogFood != null) {
        QuantityDialog(
            food = quantityDialogFood!!,
            onDismiss = { quantityDialogFood = null },
            onConfirm = { grams ->
                val f = quantityDialogFood!!
                val baseServing = f.servingSizeG ?: 100.0
                val q = grams / baseServing
                foodVm.addNutritionFood(f, q)
                quantityDialogFood = null
                onDismiss()
            }
        )
    }
}

@Composable
fun QuantityDialog(
    food: NutritionFood,
    onDismiss: () -> Unit,
    onConfirm: (grams: Double) -> Unit
) {
    var gramsText by remember {
        mutableStateOf(food.servingSizeG?.toInt()?.toString() ?: "100")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val g = gramsText.toDoubleOrNull() ?: 0.0
                if (g > 0) onConfirm(g)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text(food.food_name) },
        text = {
            Column {
                Text("How many grams?")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = gramsText,
                    onValueChange = { gramsText = it },
                    singleLine = true,
                    suffix = { Text("g") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}
