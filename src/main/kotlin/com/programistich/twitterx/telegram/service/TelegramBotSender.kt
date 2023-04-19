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
        return { bot ->
            chats.forEach { chat -> send(tweet, chat, bot) }
        }
    }

    suspend fun send(tweet: TweetData, chat: Chat, bot: AbsSender) {
        val sender = chooseSender(tweet) ?: return
        println("For tweet: $tweet choose sender: $sender")

        runCatching {
            sender.send(tweet, chat).invoke(bot)
        }.onFailure { exception ->
            println("Error $exception when send tweet: $tweet to chat: $chat")
        }
    }
}
