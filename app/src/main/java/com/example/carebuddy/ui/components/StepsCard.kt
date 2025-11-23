package com.example.carebuddy.ui.components

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.carebuddy.sensors.StepCounterManager
import java.text.NumberFormat

@Composable
fun StepsCard(
    modifier: Modifier = Modifier,
    stepGoal: Int = 8000
) {
    val context: Context = LocalContext.current
    val manager = remember { StepCounterManager(context) }
    val steps by manager.stepsToday.collectAsState(initial = 0L)
    val deviceSteps by manager.deviceSteps.collectAsState(initial = 0L)

    val progressTarget = (steps.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progressTarget)

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(progress = animatedProgress, strokeWidth = 6.dp, modifier = Modifier.fillMaxSize())
                Text("${(animatedProgress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Steps — ${NumberFormat.getIntegerInstance().format(steps)}", style = MaterialTheme.typography.titleMedium)
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                    Text("Device: ${NumberFormat.getIntegerInstance().format(deviceSteps)} • Goal: $stepGoal", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

