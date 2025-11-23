package com.example.carebuddy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.carebuddy.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedHomeScaffold(
    navController: NavHostController
) {
    val foodVm: FoodViewModel = hiltViewModel()

    var showPicker by remember { mutableStateOf(false) }
    var showMoodSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CareBuddy") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Welcome back",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Track your day in one place.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(onClick = { showPicker = true }) {
                        Text("Log food")
                    }
                    FilledTonalButton(onClick = { showMoodSheet = true }) {
                        Text("Mood check-in")
                    }
                    OutlinedButton(onClick = { navController.navigate("nutrition") }) {
                        Text("Nutrition")
                    }
                }
            }

            item { StepsCard() }
            item { HydrationCard() }
            item { FoodSummaryCard(onOpenPicker = { showPicker = true }) }
            item { MoodCard(onOpenMood = { showMoodSheet = true }) }
            item { Spacer(modifier = Modifier.height(56.dp)) }
        }
    }

    if (showPicker) {
        FoodPickerDialog(
            onDismiss = { showPicker = false }
        )
    }

    if (showMoodSheet) {
        ModalBottomSheet(onDismissRequest = { showMoodSheet = false }) {
            MoodBottomSheet(
                onDismiss = { showMoodSheet = false },
                onOpenChat = {
                    navController.navigate("chat")
                    showMoodSheet = false
                }
            )
        }
    }
}

