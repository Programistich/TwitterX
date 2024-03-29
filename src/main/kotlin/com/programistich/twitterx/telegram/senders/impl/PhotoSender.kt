package com.programistich.twitterx.telegram.senders.impl

import com.programistich.twitterx.ktx.TweetKtx.toText
import com.programistich.twitterx.models.Chat
import com.programistich.twitterx.models.TweetData
import com.programistich.twitterx.telegram.models.Priority
import com.programistich.twitterx.telegram.senders.Sender
import com.twitter.clientlib.model.Photo
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

@Component class PhotoSender : Sender {
    override fun isCanSend(data: TweetData): Priority {
        return when {
            data.media.size != 1 -> Priority.NONE
            data.media.first().type != "photo" -> Priority.NONE
            else -> Priority.HIGH
        }
    }

    override suspend fun send(data: TweetData, chat: Chat, bot: AbsSender, replyId: Int?): Message {
        val tweetPhoto = data.media.first() as Photo
        val tweetPhotoUrl = tweetPhoto.url.toString()
        val text = data.toText()

        val message = SendPhoto().apply {
            chatId = chat.chatId
            photo = InputFile(tweetPhotoUrl)
            caption = text
            replyToMessageId = replyId
        }

        return bot.execute(message)
    }
}
