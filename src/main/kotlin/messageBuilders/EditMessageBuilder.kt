package messageBuilders

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

class EditMessageBuilder private constructor(
    chatId: Long,
    messageId: Int
) {

    private val editMessage = EditMessageText().apply { 
        this.chatId = chatId.toString()
        this.messageId = messageId
    }

    fun newMessage(text: String, buttons: InlineKeyboardMarkup? = null): EditMessageBuilder {
        editMessage.text = text
        editMessage.replyMarkup = buttons ?: InlineKeyboardMarkup(listOf())
        return this
    }
    
    fun build(): EditMessageText = editMessage

    companion object {
        fun chatAndMessageId(chatId: Long, messageId: Int): EditMessageBuilder {
            return EditMessageBuilder(chatId, messageId)
        }
    }
}
