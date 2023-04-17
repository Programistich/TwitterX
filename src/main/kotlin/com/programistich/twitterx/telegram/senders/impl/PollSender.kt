package com.programistich.twitterx.telegram.senders.impl

import com.programistich.twitterx.models.Chat
import com.programistich.twitterx.models.TweetData
import com.programistich.twitterx.telegram.models.Priority
import com.programistich.twitterx.telegram.senders.Sender
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.bots.AbsSender

@Component class PollSender : Sender {
    override fun isCanSend(data: TweetData): Priority {
        return when {
            data.poll != null -> Priority.HIGH
            else -> Priority.NONE
        }
    }

    override suspend fun send(data: TweetData, chat: Chat): suspend (AbsSender) -> Unit {
        val tweetText = data.tweet.text
        val pollOptions = data.poll?.options?.map { it.label } ?: return { }

        val poll = SendPoll().apply {
            chatId = chat.chatId
            question = tweetText
            options = pollOptions
            isAnonymous = false
            isClosed = true
        }

        return { sender -> sender.execute(poll) }
    }
}
