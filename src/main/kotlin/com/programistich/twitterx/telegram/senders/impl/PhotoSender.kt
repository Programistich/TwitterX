package com.programistich.twitterx.telegram.senders

import com.programistich.twitterx.models.Chat
import com.programistich.twitterx.models.TweetData
import com.programistich.twitterx.telegram.models.Priority
import com.twitter.clientlib.model.Photo
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.bots.AbsSender

@Component class PhotoSender : Sender {
    override fun isCanSend(data: TweetData): Priority {
        val media = data.media

        // check what is only 1 photo
        if (media.size != 1) return Priority.NONE

        // check that media is photo
        if (media.first().type != "photo") return Priority.NONE
        return Priority.HIGH
    }

    override suspend fun send(data: TweetData, chat: Chat): suspend (AbsSender) -> Unit {
        val tweetPhoto = data.media.first() as Photo
        val tweetPhotoUrl = tweetPhoto.url.toString()
        val text = data.tweet.text

        val message = SendPhoto().apply {
            chatId = chat.chatId
            photo = InputFile(tweetPhotoUrl)
            caption = text
        }

//        val tweetMedias = data.media.map {
//            when (it.type) {
//                "photo" -> {
//                    val photo = it as Photo
//                    val photoUrl = photo.url.toString()
//                    InputMediaPhoto(photoUrl)
//                }
//                "video" -> {
//                    val video = it as Video
//                    val videoURL = video
//                        .variants
//                        ?.sortedBy { it.bitRate }
//                        ?.firstOrNull()
//                        ?.url
//
//                    videoURL?.let { InputMediaVideo(it.toString()) }
//                }
//                else -> null
//            }
//        }.filterNotNull()
//
//        if (tweetMedias.isEmpty()) return { }

//        val message = SendMediaGroup().apply {
//            chatId = chat.chatId
//            medias = tweetMedias
//        }

        return { sender -> sender.execute(message) }
    }
}
