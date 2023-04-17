package com.programistich.twitterx.telegram.executors.inline

import com.programistich.twitterx.ktx.Constants
import com.programistich.twitterx.telegram.executors.ExecutePriority
import com.programistich.twitterx.telegram.executors.Executor
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle
import org.telegram.telegrambots.meta.bots.AbsSender

@Component
class TwitterLinkInlineExecutor : Executor {
    override fun isCanExecute(update: Update): ExecutePriority {
        val query = update.inlineQuery?.query
        val regex = Constants.TWITTER_LINK_REGEX.toRegex()

        return when {
            query == null -> ExecutePriority.NONE
            !update.hasInlineQuery() -> ExecutePriority.NONE
            regex.matches(query) -> ExecutePriority.HIGH
            else -> ExecutePriority.NONE
        }
    }

    override suspend fun execute(update: Update): suspend (AbsSender) -> Unit {
        val queryId = update.inlineQuery?.id ?: return {}
        val query = update.inlineQuery?.query ?: return {}

        val article = InlineQueryResultArticle().apply {
            id = "1"
            title = "Twitter link"
            inputMessageContent = InputTextMessageContent().apply {
                messageText = query
            }
            url = query
            hideUrl = true
        }

        val answerInlineQuery = AnswerInlineQuery().apply {
            inlineQueryId = queryId
            results = listOf(article)
            switchPmText = "Search1"
            switchPmParameter = "search2"
        }

        return {
                telegramBot ->
            telegramBot.execute(answerInlineQuery)
        }
    }
}
