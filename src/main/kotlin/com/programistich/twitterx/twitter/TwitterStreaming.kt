package com.programistich.twitterx.twitter

import com.programistich.twitterx.repository.ChatRepository
import com.programistich.twitterx.telegram.service.TelegramBotSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import java.io.BufferedReader

@Component class TwitterStreaming(
    private val twitterService: TwitterService,
    private val telegramBotSender: TelegramBotSender,
    private val telegramBot: TelegramLongPollingBot,
    private val chatRepository: ChatRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    @EventListener(ApplicationReadyEvent::class)
    fun startStreaming() {
        val inputStream = twitterService.getStream()
        val bufferedReader = BufferedReader(inputStream.reader())
        bufferedReader.forEachLine { content ->
            if (content.isEmpty()) return@forEachLine
            val response = twitterService.getTweetByStreamJson(content)
            val chats = chatRepository.getAllChats()
            response.onSuccess { result ->
                scope.launch { telegramBotSender.send(result, chats).invoke(telegramBot) }
            }
            response.onFailure {
                println("Error when tweet stream: $it")
            }
        }
    }
}
