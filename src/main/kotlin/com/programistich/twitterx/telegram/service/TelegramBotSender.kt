package com.programistich.twitterx.telegram.service

import com.programistich.twitterx.models.Chat
import com.programistich.twitterx.models.TweetData
import com.programistich.twitterx.telegram.models.Priority
import com.programistich.twitterx.telegram.senders.Sender
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class TelegramBotSender(
    private val senders: Set<Sender>,
) {
    private fun chooseSender(tweet: TweetData): Sender? {
        return senders
            .map { it.isCanSend(tweet) to it }
            .filter { it.first != Priority.NONE }
            .maxByOrNull { it.first.value }
            ?.second
    }

    suspend fun send(tweet: TweetData, chats: List<Chat>): suspend (AbsSender) -> Unit {
        return {
            chats.forEach { chat -> send(tweet, chat) }
        }
    }

    suspend fun send(tweet: TweetData, chat: Chat): suspend (AbsSender) -> Unit {
        val sender = chooseSender(tweet) ?: return {}

        println("For tweet: $tweet choose sender: $sender")
        return { bot ->
            kotlin.runCatching {
                sender.send(tweet, chat).invoke(bot)
            }.onFailure { exception ->
                println("Error $exception when send tweet: $tweet to chat: $chat")
            }
        }
    }
}
