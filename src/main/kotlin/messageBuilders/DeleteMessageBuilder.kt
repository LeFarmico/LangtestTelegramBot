package messageBuilders

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage

class DeleteMessageBuilder private constructor(
    private val chatId: Long,
    private val messageId: Int
) {
    
    fun build(): DeleteMessage {
        return DeleteMessage().apply { 
            this.chatId = this@DeleteMessageBuilder.chatId.toString()
            this.messageId = this@DeleteMessageBuilder.messageId
        }
    }
    
    companion object {
        fun chatAndMessageId(chatId: Long, messageId: Int): DeleteMessageBuilder {
            return DeleteMessageBuilder(chatId, messageId)
        }
    }
}
