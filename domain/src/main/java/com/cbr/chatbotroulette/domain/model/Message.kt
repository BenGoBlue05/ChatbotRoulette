package com.cbr.chatbotroulette.domain.model

data class Message(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestampMs: Long
)
