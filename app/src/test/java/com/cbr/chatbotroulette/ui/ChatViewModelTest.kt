package com.cbr.chatbotroulette.ui

import com.cbr.chatbotroulette.domain.model.Message
import com.cbr.chatbotroulette.domain.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeChatRepository
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeChatRepository()
        viewModel = ChatViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial bot is from BotList`() {
        assertTrue(BotList.bots.contains(viewModel.currentBot.value))
    }

    @Test
    fun `initial messages list is empty`() = runTest {
        advanceUntilIdle()
        assertEquals(emptyList<ChatMessage>(), viewModel.messages.value)
    }

    @Test
    fun `sendMessage delegates to repository`() = runTest {
        viewModel.sendMessage("Hello")
        advanceUntilIdle()

        assertEquals(1, fakeRepository.sentMessages.size)
        assertEquals("Hello", fakeRepository.sentMessages[0].first)
    }

    @Test
    fun `sendMessage passes current bot name to repository`() = runTest {
        val botName = viewModel.currentBot.value.name
        viewModel.sendMessage("Hi")
        advanceUntilIdle()

        assertEquals(botName, fakeRepository.sentMessages[0].second)
    }

    @Test
    fun `spinForNewBot changes the current bot`() = runTest {
        val initialBot = viewModel.currentBot.value
        viewModel.spinForNewBot()
        advanceUntilIdle()

        assertNotEquals(initialBot, viewModel.currentBot.value)
    }

    @Test
    fun `spinForNewBot clears chat`() = runTest {
        viewModel.spinForNewBot()
        advanceUntilIdle()

        assertTrue(fakeRepository.chatCleared)
    }

    @Test
    fun `spinForNewBot selects bot from BotList`() = runTest {
        repeat(10) {
            viewModel.spinForNewBot()
            advanceUntilIdle()
            assertTrue(BotList.bots.contains(viewModel.currentBot.value))
        }
    }

    @Test
    fun `messages flow maps domain messages to UI models`() = runTest {
        val domainMessage = Message(
            id = "1",
            text = "Hello",
            isUser = true,
            timestampMs = 1000L
        )
        fakeRepository.emitMessages(listOf(domainMessage))
        advanceUntilIdle()

        val uiMessages = viewModel.messages.value
        assertEquals(1, uiMessages.size)
        assertEquals("1", uiMessages[0].id)
        assertEquals("Hello", uiMessages[0].text)
        assertEquals(true, uiMessages[0].isUser)
    }

    @Test
    fun `messages flow maps isUser correctly for model role`() = runTest {
        val botMessage = Message(
            id = "2",
            text = "Bot response",
            isUser = false,
            timestampMs = 2000L
        )
        fakeRepository.emitMessages(listOf(botMessage))
        advanceUntilIdle()

        val uiMessages = viewModel.messages.value
        assertEquals(false, uiMessages[0].isUser)
    }
}

class FakeChatRepository : ChatRepository {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val sentMessages = mutableListOf<Pair<String, String>>()
    var chatCleared = false

    fun emitMessages(messages: List<Message>) {
        _messages.value = messages
    }

    override fun getMessages(): Flow<List<Message>> = _messages

    override suspend fun sendMessage(text: String, currentBotName: String): Result<Unit> {
        sentMessages.add(text to currentBotName)
        return Result.success(Unit)
    }

    override suspend fun clearChat() {
        chatCleared = true
        _messages.update { emptyList() }
    }
}
