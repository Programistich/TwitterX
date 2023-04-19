package com.programistich.twitterx.telegram.service

import com.programistich.twitterx.models.Chat
import com.programistich.twitterx.models.TweetData
import com.programistich.twitterx.telegram.models.Priority
import com.programistich.twitterx.telegram.senders.Sender
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
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

    suspend fun send(tweet: List<TweetData>, chats: List<Chat>): suspend (AbsSender) -> Unit {
        return { bot ->
            chats.forEach { chat -> send(tweet, chat, bot) }
        }
    }

    suspend fun send(tweet: List<TweetData>, chat: Chat, bot: AbsSender) {
        var replayId: Int? = null
        tweet.forEach { t ->
            replayId = send(t, chat, bot, replayId)?.messageId
        }
    }

    suspend fun send(tweet: TweetData, chats: List<Chat>): suspend (AbsSender) -> Unit {
        return { bot ->
            chats.forEach { chat -> send(tweet, chat, bot) }
        }
    }

    suspend fun send(tweet: TweetData, chat: Chat, bot: AbsSender, replyId: Int? = null): Message? {
        val sender = chooseSender(tweet) ?: return null
        println("For tweet: $tweet choose sender: $sender")

        return runCatching { sender.send(tweet, chat, bot, replyId) }.getOrNull()
    }
}
