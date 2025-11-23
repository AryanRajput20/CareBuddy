package com.example.carebuddy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carebuddy.viewmodel.MoodAssistantViewModel
import com.example.carebuddy.viewmodel.MoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodBottomSheet(
    // invoked when user wants to open the chat screen (initialMessage passed)
    onOpenChat: (String) -> Unit,
    onDismiss: () -> Unit,
    vm: MoodViewModel = hiltViewModel(),
    assistantVm: MoodAssistantViewModel = hiltViewModel()
) {
    var note by remember { mutableStateOf("") }
    val emojis = listOf("😊", "🙂", "😐", "😔", "😟", "😢")
    var selected by remember { mutableStateOf(emojis[1]) }

    val isLoading by assistantVm.loading.collectAsState()
    val suggestion by assistantVm.messages.collectAsState() // messages list; suggestion shows last assistant message if exists

    Surface(
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("How are you feeling?", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                emojis.forEach { e ->
                    val s = (e == selected)
                    Surface(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable { selected = e },
                        shape = RoundedCornerShape(10.dp),
                        tonalElevation = if (s) 6.dp else 0.dp,
                        color = if (s) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(e, style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Write a short note (optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(12.dp))

            // action row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val combined = "$selected ${note.trim()}"
                        // save mood locally
                        vm.saveMood(combined)

                        // also send to assistant so it has context (non-blocking)
                        assistantVm.sendMessage(combined)

                        // navigate to chat screen with initial message
                        onOpenChat(combined)
                        onDismiss()
                    },
                    enabled = !isLoading
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Chat")
                }
            }
        }
    }
}


