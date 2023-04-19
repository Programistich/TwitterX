package com.programistich.twitterx.ktx

import com.programistich.twitterx.models.TweetData
import com.twitter.clientlib.model.AnimatedGif
import com.twitter.clientlib.model.Media
import com.twitter.clientlib.model.Photo
import com.twitter.clientlib.model.Video

object TweetKtx {
    fun Video.getBestUrl(): String? {
        return this
            .variants
            ?.sortedByDescending { it.bitRate }
            ?.firstOrNull()
            ?.url
            ?.let { return it.toString() }
    }

    fun AnimatedGif.getBestUrl(): String? {
        return this
            .variants
            ?.sortedByDescending { it.bitRate }
            ?.firstOrNull { it.contentType == "video/mp4" }
            ?.url
            ?.let { return it.toString() }
    }

    fun Photo.getBestUrl(): String? {
        return this
            .url
            ?.let { return it.toString() }
    }

    fun Media.getVideoUrl(): String? {
        return when (this) {
            is Video -> this.getBestUrl()
            is AnimatedGif -> this.getBestUrl()
            else -> null
        }
    }

    fun TweetData.toText(): String {
        val username = this.user.username
        val text = this.tweet.text
        return "Tweet by $username:\n\n$text"
    }
}
