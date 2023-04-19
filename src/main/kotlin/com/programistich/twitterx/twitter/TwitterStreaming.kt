package com.programistich.twitterx.twitter

import com.programistich.twitterx.repository.ChatRepository
import com.programistich.twitterx.telegram.service.TelegramBotSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import java.io.BufferedReader

@Component class TwitterStreaming(
    private val twitterService: TwitterService,
    private val telegramBotSender: TelegramBotSender,
    private val telegramBot: TelegramLongPollingBot,
    private val chatRepository: ChatRepository,
    @Value("\${telegram.bot.local}") private val local: String,
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    @EventListener(ApplicationReadyEvent::class)
    fun startStreaming() {
        if (local == "true") return
        processStreaming()
    }

    private fun processStreaming() {
        runCatching {
            println("Start streaming")
            val inputStream = twitterService.getStream()
            val bufferedReader = BufferedReader(inputStream.reader())
            bufferedReader.forEachLine { content ->
                if (content.isEmpty()) return@forEachLine
                val response = twitterService.getTweetBranchByStreamJson(content)
                val chats = chatRepository.getAllChats()
                response.onSuccess { result ->
                    scope.launch { telegramBotSender.send(result.reversed(), chats).invoke(telegramBot) }
                }
                response.onFailure {
                    println("Error when tweet stream: $it")
                    processStreaming()
                }
            }
        }.onFailure {
            println("Error when tweet stream: $it")
            processStreaming()
        }
    }
}
