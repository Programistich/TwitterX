package com.programistich.twitterx.telegram.senders

import com.programistich.twitterx.models.Chat
import com.programistich.twitterx.models.TweetData
import com.programistich.twitterx.telegram.models.Priority
import org.telegram.telegrambots.meta.bots.AbsSender

interface Sender {
    fun isCanSend(data: TweetData): Priority
    suspend fun send(data: TweetData, chat: Chat): suspend (AbsSender) -> Unit
}
