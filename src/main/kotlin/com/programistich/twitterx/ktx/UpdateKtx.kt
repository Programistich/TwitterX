package com.programistich.twitterx.telegram.ktx

import org.telegram.telegrambots.meta.api.objects.Update

object UpdateKtx {
    fun Update.getChatId(): String? {
        return when {
            this.hasMessage() -> this.message.chatId.toString()
            else -> null
        }
    }
}
