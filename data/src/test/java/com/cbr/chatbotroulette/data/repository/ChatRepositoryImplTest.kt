package com.cbr.chatbotroulette.data.repository

import com.cbr.chatbotroulette.data.local.dao.ChatDao
import com.cbr.chatbotroulette.data.local.entity.ChatMessageEntity
import com.cbr.chatbotroulette.data.remote.GeminiApiClient
import com.cbr.chatbotroulette.data.remote.model.Candidate
import com.cbr.chatbotroulette.data.remote.model.Content
import com.cbr.chatbotroulette.data.remote.model.GeminiResponse
import com.cbr.chatbotroulette.data.remote.model.Part
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatRepositoryImplTest {

    private lateinit var fakeDao: FakeChatDao
    private lateinit var fakeApiClient: FakeGeminiApiClient
    private lateinit var repository: ChatRepositoryImpl

    @Before
    fun setup() {
        fakeDao = FakeChatDao()
        fakeApiClient = FakeGeminiApiClient()
        repository = ChatRepositoryImpl(fakeDao, fakeApiClient)
    }

    @Test
    fun `getMessages maps entities to domain models`() = runTest {
        fakeDao.addMessage(
            ChatMessageEntity(id = 1, role = "user", content = "Hello", timestamp = 1000L)
        )
        fakeDao.addMessage(
            ChatMessageEntity(id = 2, role = "model", content = "Hi back", timestamp = 2000L)
        )

        val messages = repository.getMessages().first()

        assertEquals(2, messages.size)
        assertEquals("1", messages[0].id)
        assertEquals("Hello", messages[0].text)
        assertEquals(true, messages[0].isUser)
        assertEquals(1000L, messages[0].timestampMs)

        assertEquals("2", messages[1].id)
        assertEquals("Hi back", messages[1].text)
        assertEquals(false, messages[1].isUser)
        assertEquals(2000L, messages[1].timestampMs)
    }

    @Test
    fun `sendMessage inserts user message into dao`() = runTest {
        fakeApiClient.responseToReturn = Result.success(
            GeminiResponse(
                candidates = listOf(
                    Candidate(Content("model", listOf(Part("Bot reply"))))
                )
            )
        )

        repository.sendMessage("Hello", "TestBot")

        val inserted = fakeDao.insertedMessages
        assertTrue(inserted.any { it.role == "user" && it.content == "Hello" })
    }

    @Test
    fun `sendMessage inserts AI response on success`() = runTest {
        fakeApiClient.responseToReturn = Result.success(
            GeminiResponse(
                candidates = listOf(
                    Candidate(Content("model", listOf(Part("Bot reply"))))
                )
            )
        )

        repository.sendMessage("Hello", "TestBot")

        val inserted = fakeDao.insertedMessages
        assertTrue(inserted.any { it.role == "model" && it.content == "Bot reply" })
    }

    @Test
    fun `sendMessage uses fallback text when response has no candidates`() = runTest {
        fakeApiClient.responseToReturn = Result.success(
            GeminiResponse(candidates = null)
        )

        repository.sendMessage("Hello", "TestBot")

        val inserted = fakeDao.insertedMessages
        assertTrue(inserted.any { it.role == "model" && it.content == "..." })
    }

    @Test
    fun `sendMessage does not insert AI message on API failure`() = runTest {
        fakeApiClient.responseToReturn = Result.failure(Exception("Network error"))

        repository.sendMessage("Hello", "TestBot")

        val modelMessages = fakeDao.insertedMessages.filter { it.role == "model" }
        assertTrue(modelMessages.isEmpty())
    }

    @Test
    fun `sendMessage still inserts user message on API failure`() = runTest {
        fakeApiClient.responseToReturn = Result.failure(Exception("Network error"))

        repository.sendMessage("Hello", "TestBot")

        val userMessages = fakeDao.insertedMessages.filter { it.role == "user" }
        assertEquals(1, userMessages.size)
        assertEquals("Hello", userMessages[0].content)
    }

    @Test
    fun `sendMessage returns success even on API failure`() = runTest {
        fakeApiClient.responseToReturn = Result.failure(Exception("Network error"))

        val result = repository.sendMessage("Hello", "TestBot")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `sendMessage builds correct system prompt with bot name`() = runTest {
        fakeApiClient.responseToReturn = Result.success(
            GeminiResponse(
                candidates = listOf(
                    Candidate(Content("model", listOf(Part("Reply"))))
                )
            )
        )

        repository.sendMessage("Hello", "Pirate-Bot")

        val contents = fakeApiClient.lastContents!!
        assertTrue(contents[0].parts[0].text.contains("Pirate-Bot"))
    }

    @Test
    fun `clearChat delegates to dao`() = runTest {
        repository.clearChat()
        assertTrue(fakeDao.cleared)
    }
}

class FakeChatDao : ChatDao {
    private val _messages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val insertedMessages = mutableListOf<ChatMessageEntity>()
    var cleared = false

    fun addMessage(entity: ChatMessageEntity) {
        _messages.update { it + entity }
    }

    override fun getAllMessages(): Flow<List<ChatMessageEntity>> = _messages

    override suspend fun insertMessage(message: ChatMessageEntity) {
        insertedMessages.add(message)
        _messages.update { it + message }
    }

    override suspend fun clearAllMessages() {
        cleared = true
        _messages.update { emptyList() }
    }
}

class FakeGeminiApiClient : GeminiApiClient(
    httpClient = io.ktor.client.HttpClient()
) {
    var responseToReturn: Result<GeminiResponse> = Result.success(GeminiResponse(candidates = emptyList()))
    var lastContents: List<Content>? = null

    override suspend fun generateContent(contents: List<Content>): Result<GeminiResponse> {
        lastContents = contents
        return responseToReturn
    }
}
