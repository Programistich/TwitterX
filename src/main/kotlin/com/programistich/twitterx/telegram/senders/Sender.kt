package com.programistich.twitterx.telegram.sender

import com.programistich.twitterx.telegram.models.Priority
import org.telegram.telegrambots.meta.bots.AbsSender

interface Sender {
    fun isCanSend(): Priority
    fun send(): suspend (AbsSender) -> Unit
}
