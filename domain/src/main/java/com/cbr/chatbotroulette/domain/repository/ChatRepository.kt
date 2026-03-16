package com.cbr.chatbotroulette.domain.repository

import com.cbr.chatbotroulette.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(): Flow<List<Message>>
    suspend fun sendMessage(text: String, currentBotName: String): Result<Unit>
    suspend fun clearChat()
}
