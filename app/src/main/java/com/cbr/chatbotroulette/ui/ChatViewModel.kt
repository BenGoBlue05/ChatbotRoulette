package com.cbr.chatbotroulette.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cbr.chatbotroulette.domain.model.Message
import com.cbr.chatbotroulette.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _currentBot = MutableStateFlow(BotList.bots.random())
    val currentBot: StateFlow<BotInfo> = _currentBot

    val messages: StateFlow<List<ChatMessage>> = chatRepository.getMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        .mapListToUiModels()

    fun sendMessage(text: String) {
        viewModelScope.launch {
            chatRepository.sendMessage(text, _currentBot.value.name)
        }
    }

    fun spinForNewBot() {
        viewModelScope.launch {
            chatRepository.clearChat()
            var nextBot = BotList.bots.random()
            while (nextBot == _currentBot.value && BotList.bots.size > 1) {
                nextBot = BotList.bots.random()
            }
            _currentBot.update { nextBot }
        }
    }

    private fun kotlinx.coroutines.flow.Flow<List<Message>>.mapListToUiModels(): StateFlow<List<ChatMessage>> {
        val mappedFlow = MutableStateFlow<List<ChatMessage>>(emptyList())
        viewModelScope.launch {
            this@mapListToUiModels.collect { domainMessages ->
                mappedFlow.value = domainMessages.map { it.toUiModel() }
            }
        }
        return mappedFlow
    }

    private fun Message.toUiModel(): ChatMessage {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return ChatMessage(
            id = this.id,
            text = this.text,
            isUser = this.isUser,
            timestamp = sdf.format(Date(this.timestampMs))
        )
    }
}

data class BotInfo(val name: String, val description: String)

object BotList {
    val bots = listOf(
        BotInfo("Zany-Bot 3000", "Loves random facts and silly jokes."),
        BotInfo("Shakespeare-Bot", "Speaks only in dramatic prose and iambic pentameter."),
        BotInfo("Pirate-Bot", "Ahoy! Yer talking to a pirate."),
        BotInfo("Grandma-Bot", "Very sweet, always asks if you've eaten enough."),
        BotInfo("Sarcastic-Bot", "Doesn't really want to be here.")
    )
}
