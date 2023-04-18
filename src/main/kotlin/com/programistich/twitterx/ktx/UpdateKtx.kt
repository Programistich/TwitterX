package com.programistich.twitterx.ktx

import org.telegram.telegrambots.meta.api.objects.Update

object UpdateKtx {
    fun Update.getChatId(): String? {
        return when {
            this.hasMessage() -> this.message.chatId.toString()
            this.hasChannelPost() -> this.channelPost.chatId.toString()
            else -> null
        }
    }
}
