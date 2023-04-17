package com.programistich.twitterx.telegram.senders

import com.programistich.twitterx.ktx.getBestUrl
import com.programistich.twitterx.models.Chat
import com.programistich.twitterx.models.TweetData
import com.programistich.twitterx.telegram.models.Priority
import com.twitter.clientlib.model.Video
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.bots.AbsSender

@Component class VideoSender : Sender {
    override fun isCanSend(data: TweetData): Priority {
        val media = data.media

        // check what is only 1 photo
        if (media.size != 1) return Priority.NONE

        // check that media is photo
        if (media.first().type != "video") return Priority.NONE
        return Priority.HIGH
    }

    override suspend fun send(data: TweetData, chat: Chat): suspend (AbsSender) -> Unit {
        val tweetVideo = data.media.first() as Video
        val tweetPhotoUrl = tweetVideo.getBestUrl()
        val text = data.tweet.text

        val message = SendVideo().apply {
            chatId = chat.chatId
            video = InputFile(tweetPhotoUrl)
            caption = text
        }

        return { sender -> sender.execute(message) }
    }
}
