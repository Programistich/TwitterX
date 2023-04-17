CREATE TABLE IF NOT EXISTS chats (
    id SERIAL NOT NULL,
    chat_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (id, chat_id)
);