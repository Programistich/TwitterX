package com.programistich.twitterx.telegram.senders

import com.programistich.twitterx.models.Chat
import com.programistich.twitterx.models.TweetData
import com.programistich.twitterx.telegram.models.Priority
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class TextSender : Sender {
    override fun isCanSend(data: TweetData): Priority {
        if (data.poll == null || data.media.isEmpty()) {
            return Priority.HIGH
        }

        if (data.tweet.text.isEmpty()) {
            return Priority.NONE
        }

        return Priority.NORMAL
    }

    override suspend fun send(data: TweetData, chat: Chat): suspend (AbsSender) -> Unit {
         val tweetText = data.tweet.text

        val message = SendMessage().apply {
            chatId = chat.chatId
            text = tweetText
        }

        return { sender -> sender.execute(message) }
    }

}
