package com.example.carebuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carebuddy.data.remote.AiApi
import com.example.carebuddy.data.remote.ChatMessageDto
import com.example.carebuddy.data.remote.ChatRequest
import com.example.carebuddy.data.remote.ChatResponse
import com.example.carebuddy.models.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoodAssistantViewModel @Inject constructor(
    private val aiApi: AiApi
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun sendMessage(text: String) {
        val userMsg = ChatMessage(role = ChatMessage.Role.USER, text = text)
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            try {
                _loading.value = true

                val dto = ChatMessageDto(role = "user", text = text)
                val req = ChatRequest(conversation = listOf(dto))

                val res: ChatResponse = aiApi.chat(req)

                val assistantMsg = ChatMessage(
                    role = ChatMessage.Role.ASSISTANT,
                    text = res.reply
                )
                _messages.value = _messages.value + assistantMsg
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    role = ChatMessage.Role.ASSISTANT,
                    text = "Sorry — can't reach server."
                )
            } finally {
                _loading.value = false
            }
        }
    }
}


