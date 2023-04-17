package com.programistich.twitterx.repository

import com.programistich.twitterx.models.Chat
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component class ChatRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    fun create(chatId: String) {
        jdbcTemplate.update(
            """
                INSERT INTO chats(chat_id)
                SELECT (?)
                    WHERE NOT EXISTS (
                    SELECT 1 FROM chats WHERE chat_id=(?)
                );
            """.trimIndent(),
            chatId,
            chatId
        )
    }

    fun getAllChats(): List<Chat> {
        return jdbcTemplate.query(
            """
                SELECT id, chat_id
                FROM chats
            """.trimIndent()
        ) { rs, _ ->
            Chat(
                chatId = rs.getString("chat_id")
            )
        }
    }

    fun get(chatId: String): Chat? {
        return jdbcTemplate.query(
            "SELECT id, chat_id FROM chats WHERE chat_id = (?)",
            { rs, _ ->
                Chat(chatId = rs.getString("chat_id"))
            },
            chatId,
        ).firstOrNull()
    }
}
