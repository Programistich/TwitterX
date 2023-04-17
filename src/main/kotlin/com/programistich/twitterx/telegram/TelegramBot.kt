package com.programistich.twitterx.telegram

import com.programistich.twitterx.telegram.service.TelegramBotHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
@Component class TelegramBot(
    @Value("\${telegram.bot.token}") private val token: String,
    @Value("\${telegram.bot.username}") private val username: String,
    private val telegramBotHandler: TelegramBotHandler
) {
    @Bean fun createTelegramBot(telegramLongPollingBot: TelegramLongPollingBot): TelegramBotsApi {
        return TelegramBotsApi(DefaultBotSession::class.java).apply {
            registerBot(telegramLongPollingBot)
        }
    }

    @Bean fun createTelegramBotHandler(): TelegramLongPollingBot {
        return object : TelegramLongPollingBot(token) {
            override fun getBotUsername() = username
            override fun onUpdateReceived(update: Update?) {
                telegramBotHandler.process(update, this)
            }
        }
    }
}
