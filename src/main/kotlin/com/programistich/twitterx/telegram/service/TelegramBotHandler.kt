package com.programistich.twitterx.telegram.service

import com.programistich.twitterx.ktx.UpdateKtx.getChatId
import com.programistich.twitterx.repository.ChatRepository
import com.programistich.twitterx.telegram.executors.Executor
import com.programistich.twitterx.telegram.models.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

@Component class TelegramBotHandler(
    private val executors: Set<Executor>,
    private val chatRepository: ChatRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun process(update: Update?, bot: AbsSender) {
        if (update == null) return

        scope.launch {
            runCatching {
                processExecutorUpdate(update, bot)
            }.onFailure { error ->
                error.printStackTrace()
            }
        }
    }

    private suspend fun processExecutorUpdate(update: Update, bot: AbsSender) = withContext(Dispatchers.Default) {
        createChat(update)

        val executor = executors
            .map { it.isCanExecute(update) to it }
            .filter { it.first != Priority.NONE }
            .maxByOrNull { it.first.value }
            ?.second
            ?: return@withContext

        println("For update: $update choose executor: $executor")
        executor.execute(update).invoke(bot)
    }

    private fun createChat(update: Update) {
        val chatId = update.getChatId() ?: return
        chatRepository.create(chatId)
    }
}
