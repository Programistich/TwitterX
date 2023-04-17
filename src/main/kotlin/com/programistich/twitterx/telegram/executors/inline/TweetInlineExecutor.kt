package com.programistich.twitterx.telegram.executors.inline

import com.programistich.twitterx.ktx.Constants
import com.programistich.twitterx.telegram.executors.Executor
import com.programistich.twitterx.telegram.models.Priority
import com.programistich.twitterx.twitter.TwitterService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

@Component class TweetInlineExecutor(
    private val twitterService: TwitterService,
) : Executor {
    override fun isCanExecute(update: Update): Priority {
        val query = update.inlineQuery?.query

        return when {
            query == null -> Priority.NONE
            !update.hasInlineQuery() -> Priority.NONE
            Constants.TWEET_LINK_REGEX.matches(query) -> Priority.HIGH
            else -> Priority.NONE
        }
    }

    override suspend fun execute(update: Update): suspend (AbsSender) -> Unit {
//        val queryId = update.inlineQuery?.id ?: return {}
//        val query = update.inlineQuery?.query ?: return {}
//
//        val match = Constants.TWEET_LINK_REGEX.find(query) ?: return {}
//        val tweetId = match.groupValues[Constants.TWEET_LINK_REGEX_GROUP_TWEET_ID]
//        val tweets = twitterService.getTweetById(tweetId)
//

        return {}
    }
}
