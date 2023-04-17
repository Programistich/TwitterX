package com.programistich.twitterx.models

import com.twitter.clientlib.model.Media
import com.twitter.clientlib.model.Poll
import com.twitter.clientlib.model.Tweet
import com.twitter.clientlib.model.User

data class TweetData(
    val tweet: Tweet,
    val poll: Poll?,
    val user: User,
    val media: List<Media>,
)