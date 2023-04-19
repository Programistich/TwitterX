package com.programistich.twitterx.twitter

import com.programistich.twitterx.models.TweetData
import com.twitter.clientlib.api.TwitterApi
import com.twitter.clientlib.model.AddOrDeleteRulesRequest
import com.twitter.clientlib.model.AddRulesRequest
import com.twitter.clientlib.model.DeleteRulesRequest
import com.twitter.clientlib.model.DeleteRulesRequestDelete
import com.twitter.clientlib.model.Problem
import com.twitter.clientlib.model.RuleNoId
import com.twitter.clientlib.model.StreamingTweetResponse
import org.springframework.stereotype.Component
import java.io.InputStream

@Component class TwitterService(
    private val twitterApi: TwitterApi
) {
    val tweetFields = setOf(
        "attachments",
        "author_id",
        "created_at",
        "entities",
        "lang",
        "referenced_tweets",
        "source",
        "text",
        "geo",
        "conversation_id",
        "in_reply_to_user_id",
    )

    val userFields = setOf(
        "created_at",
        "description",
        "entities",
        "url",
        "verified"
    )

    val pollFields = setOf(
        "duration_minutes",
        "end_datetime",
        "id",
        "options",
        "voting_status"
    )

    val expansions = setOf(
        "author_id",
        "attachments.poll_ids",
        "attachments.media_keys"
    )

    val mediaFields = setOf(
        "url",
        "variants"
    )

    private var inputStream: InputStream? = null

    fun getTweetById(tweetId: String): Result<TweetData> {
        return runCatching {
            val response = twitterApi
                .tweets()
                .findTweetById(tweetId)
                .tweetFields(tweetFields)
                .userFields(userFields)
                .expansions(expansions)
                .pollFields(pollFields)
                .mediaFields(mediaFields)
                .execute()

            if (existError(response.errors)) {
                return Result.failure(Exception(response.errors?.joinToString { it.title }))
            }

            val tweet = response.data ?: return Result.failure(Exception("Tweet not found"))
            val user = response
                .includes
                ?.users
                ?.firstOrNull()
                ?: return Result.failure(Exception("User not found"))

            val poll = response
                .includes
                ?.polls
                ?.firstOrNull()

            val media = response
                .includes
                ?.media
                ?: emptyList()

            val data = TweetData(tweet, poll, user, media)
            return Result.success(data)
        }
    }

    fun getTweetByStreamJson(json: String): Result<TweetData> {
        return runCatching {
            val response = StreamingTweetResponse.fromJson(json) ?: return Result.failure(Exception("Tweet not found"))

            if (existError(response.errors)) {
                return Result.failure(Exception(response.errors?.joinToString { it.title }))
            }

            val tweet = response.data ?: return Result.failure(Exception("Tweet not found"))
            val user = response
                .includes
                ?.users
                ?.firstOrNull()
                ?: return Result.failure(Exception("User not found"))

            val poll = response
                .includes
                ?.polls
                ?.firstOrNull()

            val media = response
                .includes
                ?.media
                ?: emptyList()

            val data = TweetData(tweet, poll, user, media)
            return Result.success(data)
        }
    }

    private fun existError(errors: List<Problem>?): Boolean {
        return errors?.isNotEmpty() ?: false
    }

    fun deleteAllRules() {
        val result = twitterApi.tweets().rules.execute().data ?: emptyList()
        if (result.isEmpty()) return

        val request = DeleteRulesRequestDelete().ids(result.map { it.id })
        val deleteRequest = DeleteRulesRequest().delete(request)
        val addOrDeleteRulesRequest = AddOrDeleteRulesRequest(deleteRequest)

        twitterApi.tweets().addOrDeleteRules(addOrDeleteRulesRequest).execute()
    }

    fun addRules(values: List<String>) {
        val rules = values.map { RuleNoId().value("from:$it") }

        val request = AddRulesRequest().add(rules)
        val addOrDeleteRulesRequest = AddOrDeleteRulesRequest(request)

        twitterApi.tweets().addOrDeleteRules(addOrDeleteRulesRequest).execute().also { response ->
            println("Current rules ${response.data?.map { it.value }}")
        }
    }

    fun getStream(rules: List<String> = listOf("elonmusk")): InputStream {
        inputStream?.close()

        deleteAllRules()
        addRules(rules)

        val localTwitterApi = twitterApi
            .tweets()
            .searchStream()
            .tweetFields(tweetFields)
            .userFields(userFields)
            .expansions(expansions)
            .mediaFields(mediaFields)
            .pollFields(pollFields)
            .execute()

        inputStream = localTwitterApi
        return localTwitterApi
    }
}
