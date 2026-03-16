package com.cbr.chatbotroulette

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.cbr.chatbotroulette.ui.ChatScreen
import com.cbr.chatbotroulette.ui.ChatViewModel
import com.cbr.chatbotroulette.ui.theme.ChatbotRouletteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val messages by viewModel.messages.collectAsState()
            val currentBot by viewModel.currentBot.collectAsState()

            ChatbotRouletteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen(
                        messages = messages,
                        botName = currentBot.name,
                        onSend = { text ->
                            viewModel.sendMessage(text)
                        },
                        onSpinClick = {
                            viewModel.spinForNewBot()
                        }
                    )
                }
            }
        }
    }
}
