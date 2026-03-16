package com.cbr.chatbotroulette.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cbr.chatbotroulette.R
import com.cbr.chatbotroulette.ui.theme.ChatbotRouletteTheme
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    botName: String,
    onSend: (String) -> Unit,
    onSpinClick: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFF0F8FF), // Very light blue/white
        bottomBar = {
            ChatInput(onSend = onSend)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            ChatHeaderSection(botName = botName, onSpinClick = onSpinClick)
            
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 40.dp,
                    bottom = 24.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            ) {
                items(messages) { msg ->
                    ChatMessageItem(msg = msg)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ChatHeaderSection(botName: String, onSpinClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Gradient Header Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0056D2), // Dark vivid blue
                            Color(0xFF9C27B0)  // Purple
                        )
                    )
                )
                .padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp,
                    bottom = 48.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Image(
                    painter = painterResource(id = R.drawable.ic_robot_avatar),
                    contentDescription = "Robot Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.Cyan.copy(alpha = 0.5f), CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    // Chatbot Roulette Title Bubble
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 4.dp
                        ),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = "Chatbot\nRoulette",
                            color = Color(0xFF1E1E1E),
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            lineHeight = 32.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Subtitle
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 4.dp
                        ),
                        color = Color(0xFFE0F7FA), // Light cyan
                        shadowElevation = 2.dp
                    ) {
                        Text(
                            text = "You are chatting with:\n$botName",
                            color = Color(0xFF006064),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Spin Button Overlapping
        Surface(
            onClick = onSpinClick,
            shape = RoundedCornerShape(50),
            color = Color(0xFFFFCA28), // Yellow
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp)
                .offset(y = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp, end = 24.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFF7043), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = "Spin",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("Spin for New Bot", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ChatMessageItem(msg: ChatMessage) {
    val isUser = msg.isUser
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isUser) {
                // Small robot icon
                Icon(
                    imageVector = Icons.Rounded.SmartToy,
                    contentDescription = null,
                    tint = Color(0xFF003B73),
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Surface(
                shape = if (isUser) {
                    RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
                } else {
                    RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
                },
                color = if (isUser) Color(0xFF0056D2) else Color(0xFFE1BEE7), // user: blue, bot: light purple
                shadowElevation = 2.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = msg.text,
                    color = if (isUser) Color.White else Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = msg.timestamp,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(
                start = if (isUser) 0.dp else 36.dp,
                end = if (isUser) 8.dp else 0.dp
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFFE1BEE7).copy(alpha = 0.3f),
                            Color(0xFFE1BEE7).copy(alpha = 0.6f)
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Type a message...") },
                leadingIcon = {
                    Icon(Icons.Rounded.SentimentSatisfied, contentDescription = "Emoji", tint = Color.Gray)
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(50)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF0056D2),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFFFCA28), CircleShape)
                    .clickable { 
                        if (text.isNotBlank()) {
                            onSend(text)
                            text = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Send",
                    tint = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7_pro")
@Composable
fun ChatScreenPreview() {
    ChatbotRouletteTheme {
        ChatScreen(
            messages = listOf(
                ChatMessage("1", "Hello! What's your specialty today?", true, "10:05 AM"),
                ChatMessage("2", "Greetings, human! I specialize in telling random, delightful facts!", false, "10:05 AM"),
                ChatMessage("3", "Hello! What's your specialty today?", true, "10:05 AM"),
                ChatMessage("4", "Hey sings, hope? I'/l're, noth nearby ontaidean, you set and mim number!", false, "10:05 AM")
            ),
            botName = "Zany-Bot 3000",
            onSend = {},
            onSpinClick = {}
        )
    }
}
