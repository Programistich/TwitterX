package com.programistich.twitterx.telegram.senders

import com.programistich.twitterx.ktx.getBestUrl
import com.programistich.twitterx.models.Chat
import com.programistich.twitterx.models.TweetData
import com.programistich.twitterx.telegram.models.Priority
import com.twitter.clientlib.model.Photo
import com.twitter.clientlib.model.Video
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo
import org.telegram.telegrambots.meta.bots.AbsSender

@Component class ManyMediaSender : Sender {
    override fun isCanSend(data: TweetData): Priority {
        if (data.media.size >= 2) return Priority.HIGH
        return Priority.NONE
    }

    override suspend fun send(data: TweetData, chat: Chat): suspend (AbsSender) -> Unit {
        val tweetMedias = data.media.mapNotNull {
            when (it.type) {
                "photo" -> {
                    val photo = it as Photo
                    val url = photo.getBestUrl()
                    url?.let { InputMediaPhoto(it) }
                }

                "video" -> {
                    val video = it as Video
                    val url = video.getBestUrl()
                    url?.let { InputMediaVideo(it) }
                }

                else -> null
            }
        }

        if (tweetMedias.isEmpty()) return { }

        val message = SendMediaGroup().apply {
            chatId = chat.chatId
            medias = tweetMedias
        }

        return { sender -> sender.execute(message) }
    }
}
