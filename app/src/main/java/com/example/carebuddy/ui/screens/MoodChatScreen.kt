package com.example.carebuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.carebuddy.models.ChatMessage
import com.example.carebuddy.viewmodel.MoodAssistantViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodChatScreen(
    initialMessage: String = "",
    onBack: () -> Unit = {},
    vm: MoodAssistantViewModel = hiltViewModel()
) {
    val messages by vm.messages.collectAsState()
    val loading by vm.loading.collectAsState()

    // auto-send initialMessage once when screen appears
    var sentInitial by remember { mutableStateOf(false) }
    LaunchedEffect(initialMessage) {
        if (initialMessage.isNotBlank() && !sentInitial) {
            vm.sendMessage(initialMessage)
            sentInitial = true
        }
    }

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CareBuddy Assistant") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(inner)) {

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                itemsIndexed(messages, key = { _, m -> m.id }) { _, msg ->
                    ChatRow(msg)
                }
            }

            // input area
            ChatInputBar(
                isSending = loading,
                onSend = { text -> vm.sendMessage(text) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }

    // auto scroll on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
}

@Composable
private fun ChatRow(msg: ChatMessage) {
    val mine = msg.role == ChatMessage.Role.USER
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
    ) {
        if (!mine) {
            Surface(
                shape = MaterialTheme.shapes.small,
                tonalElevation = 2.dp,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("AI", modifier = Modifier.padding(8.dp))
            }
        }

        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 1.dp,
            modifier = Modifier
                .padding(horizontal = 4.dp)
        ) {
            Column(modifier = Modifier
                .background(if (mine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (mine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ChatInputBar(isSending: Boolean, onSend: (String) -> Unit, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    val canSend = text.trim().isNotEmpty() && !isSending

    Surface(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                placeholder = { Text("Share how you're feeling…") },
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    val m = text.trim()
                    if (m.isNotEmpty() && !isSending) {
                        onSend(m)
                        text = ""
                    }
                },
                enabled = canSend
            ) {
                if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

