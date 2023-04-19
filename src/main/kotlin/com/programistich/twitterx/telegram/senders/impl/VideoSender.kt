package com.programistich.twitterx.telegram.senders.impl

import com.programistich.twitterx.ktx.TweetKtx.getVideoUrl
import com.programistich.twitterx.ktx.TweetKtx.toText
import com.programistich.twitterx.models.Chat
import com.programistich.twitterx.models.TweetData
import com.programistich.twitterx.telegram.models.Priority
import com.programistich.twitterx.telegram.senders.Sender
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.bots.AbsSender

@Component class VideoSender : Sender {
    override fun isCanSend(data: TweetData): Priority {
        return when {
            data.media.size != 1 -> Priority.NONE
            data.media.first().type == "video" -> Priority.HIGH
            data.media.first().type == "animated_gif" -> Priority.HIGH
            else -> Priority.NONE
        }
    }

    override suspend fun send(data: TweetData, chat: Chat): suspend (AbsSender) -> Unit {
        val tweetVideo = data.media.first().getVideoUrl()

        val message = SendVideo().apply {
            chatId = chat.chatId
            video = InputFile(tweetVideo)
            caption = data.toText()
        }

        return { sender -> sender.execute(message) }
    }
}
