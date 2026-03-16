package com.cbr.chatbotroulette.data.repository

import com.cbr.chatbotroulette.data.local.dao.ChatDao
import com.cbr.chatbotroulette.data.local.entity.ChatMessageEntity
import com.cbr.chatbotroulette.data.remote.GeminiApiClient
import com.cbr.chatbotroulette.data.remote.model.Content
import com.cbr.chatbotroulette.data.remote.model.Part
import com.cbr.chatbotroulette.domain.model.Message
import com.cbr.chatbotroulette.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val geminiApiClient: GeminiApiClient
) : ChatRepository {

    override fun getMessages(): Flow<List<Message>> {
        return chatDao.getAllMessages().map { entities ->
            entities.map { entity ->
                Message(
                    id = entity.id.toString(),
                    text = entity.content,
                    isUser = entity.role == "user",
                    timestampMs = entity.timestamp
                )
            }
        }
    }

    override suspend fun sendMessage(text: String, currentBotName: String): Result<Unit> {
        val userMessage = ChatMessageEntity(
            role = "user",
            content = text,
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertMessage(userMessage)

        val systemPrompt = "You are an AI named $currentBotName. Answer in character. Only output your response to the user's latest input."
        val contents = listOf(
            Content("user", listOf(Part(systemPrompt))),
            Content("model", listOf(Part("Understood. I will answer as $currentBotName."))),
            Content("user", listOf(Part(text)))
        )

        val result = geminiApiClient.generateContent(contents)
        if (result.isSuccess) {
             val response = result.getOrNull()
             val aiText = response?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "..."
             val aiMessage = ChatMessageEntity(
                 role = "model",
                 content = aiText,
                 timestamp = System.currentTimeMillis()
             )
             chatDao.insertMessage(aiMessage)
        }
        return Result.success(Unit)
    }

    override suspend fun clearChat() {
        chatDao.clearAllMessages()
    }
}
