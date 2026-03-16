package com.cbr.chatbotroulette.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BotListTest {

    @Test
    fun `BotList contains exactly 5 bots`() {
        assertEquals(5, BotList.bots.size)
    }

    @Test
    fun `all bots have non-empty names`() {
        BotList.bots.forEach { bot ->
            assertTrue("Bot name should not be blank", bot.name.isNotBlank())
        }
    }

    @Test
    fun `all bots have non-empty descriptions`() {
        BotList.bots.forEach { bot ->
            assertTrue("Bot description should not be blank", bot.description.isNotBlank())
        }
    }

    @Test
    fun `all bot names are unique`() {
        val names = BotList.bots.map { it.name }
        assertEquals(names.size, names.toSet().size)
    }

    @Test
    fun `BotInfo data class equality works correctly`() {
        val bot1 = BotInfo("TestBot", "A test bot")
        val bot2 = BotInfo("TestBot", "A test bot")
        assertEquals(bot1, bot2)
    }
}
