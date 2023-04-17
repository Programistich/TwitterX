package com.programistich.twitterx

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@Suppress("UtilityClassWithPublicConstructor")
@SpringBootApplication
@EnableScheduling
class TwitterXApplication {
    companion object {
        @Suppress("SpreadOperator")
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<TwitterXApplication>(*args)
        }
    }
}
