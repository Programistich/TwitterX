package com.programistich.twitterx.telegram.executors

import com.programistich.twitterx.telegram.models.Priority
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

interface Executor {
    fun isCanExecute(update: Update): Priority
    suspend fun execute(update: Update): suspend (AbsSender) -> Unit
}
