package com.programistich.twitterx.ktx

object Constants {
    val TWEET_LINK_REGEX = "https://twitter.com/([a-zA-Z0-9_]+)/status/([0-9]+)?(.*)".toRegex()
    const val TWEET_LINK_REGEX_GROUP_TWEET_ID = 2
}
