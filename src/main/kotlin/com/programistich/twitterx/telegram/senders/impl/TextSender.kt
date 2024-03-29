package com.programistich.twitterx.telegram.senders.impl

import com.programistich.twitterx.ktx.TweetKtx.toText
import com.programistich.twitterx.models.Chat
import com.programistich.twitterx.models.TweetData
import com.programistich.twitterx.telegram.models.Priority
import com.programistich.twitterx.telegram.senders.Sender
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class TextSender : Sender {
    override fun isCanSend(data: TweetData): Priority {
        return when {
            data.media.isNotEmpty() -> Priority.NONE
            data.poll != null -> Priority.NONE
            data.tweet.text.isEmpty() -> Priority.NONE
            else -> Priority.NORMAL
        }
    }

    override suspend fun send(data: TweetData, chat: Chat, bot: AbsSender, replyId: Int?): Message {
        val message = SendMessage().apply {
            chatId = chat.chatId
            text = data.toText()
            replyToMessageId = replyId
        }

        return bot.execute(message)
    }
}
