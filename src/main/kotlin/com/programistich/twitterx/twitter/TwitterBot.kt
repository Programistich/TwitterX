package com.programistich.twitterx.twitter

import com.twitter.clientlib.TwitterCredentialsBearer
import com.twitter.clientlib.api.TwitterApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component class TwitterBot(
    @Value("\${twitter.bearer.token}") private val bearerToken: String,
) {
    @Bean
    fun createTwitterApi(): TwitterApi {
        return TwitterApi(TwitterCredentialsBearer(bearerToken))
    }
}
