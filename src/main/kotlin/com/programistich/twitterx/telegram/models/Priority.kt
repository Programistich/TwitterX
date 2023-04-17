package com.programistich.twitterx.telegram.executors

enum class ExecutePriority(val value: Int) {
    NONE(-1),
    LOW(0),
    NORMAL(1),
    HIGH(2)
}
