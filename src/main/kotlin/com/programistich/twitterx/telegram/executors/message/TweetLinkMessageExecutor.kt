package com.programistich.twitterx.telegram.executors.message

import com.programistich.twitterx.ktx.Constants
import com.programistich.twitterx.repository.ChatRepository
import com.programistich.twitterx.telegram.executors.Executor
import com.programistich.twitterx.telegram.models.Priority
import com.programistich.twitterx.telegram.service.TelegramBotSender
import com.programistich.twitterx.twitter.TwitterService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

@Component class TweetLinkMessageExecutor(
    private val twitterService: TwitterService,
    private val chatRepository: ChatRepository,
    private val telegramBotSender: TelegramBotSender
) : Executor {
    override fun isCanExecute(update: Update): Priority {
        val message = update.message?.text
        return when {
            message == null -> Priority.NONE
            !update.hasMessage() -> Priority.NONE
            Constants.TWEET_LINK_REGEX.matches(message) -> Priority.HIGH
            else -> Priority.NONE
        }
    }

    override suspend fun execute(update: Update): suspend (AbsSender) -> Unit {
        val chatId = update.message?.chatId ?: return {}
        val chat = chatRepository.get(chatId.toString()) ?: return {}

        val message = update.message?.text ?: return {}

        val match = Constants.TWEET_LINK_REGEX.find(message) ?: return {}
        val tweetId = match.groupValues[Constants.TWEET_LINK_REGEX_GROUP_TWEET_ID]
        val tweets = twitterService.getTweetById(tweetId)

        return { bot ->
            tweets.onSuccess {
                val sender = telegramBotSender.send(it, chat)
                sender.invoke(bot)
            }
        }
    }
}
