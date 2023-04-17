package com.programistich.twitterx.telegram

import com.programistich.twitterx.telegram.executors.ExecutePriority
import com.programistich.twitterx.telegram.executors.Executor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

@Component class TelegramBotHandler(private val executors: Set<Executor>) {
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
        val executor = executors
            .map { it.isCanExecute(update) to it }
            .filter { it.first != ExecutePriority.NONE }
            .maxByOrNull { it.first.value }
            ?.second
            ?: return@withContext

        println("For update: $update choose executor: $executor")
        executor.execute(update).invoke(bot)
    }
}
