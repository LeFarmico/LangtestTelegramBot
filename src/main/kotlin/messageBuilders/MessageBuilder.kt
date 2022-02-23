package messageBuilders

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

class MessageBuilder(private val chatId: Long) {
    
    private val sendMessage = SendMessage().apply { 
        chatId = this@MessageBuilder.chatId.toString()
    }
    
    fun setText(text: String): MessageBuilder {
        sendMessage.text = text
        return this
    }
    
    fun setButtons(inlineKeyboardMarkup: InlineKeyboardMarkup): MessageBuilder {
        sendMessage.replyMarkup = inlineKeyboardMarkup
        return this
    }

    fun setButtons(inlineKeyboardMarkup: () -> InlineKeyboardMarkup): MessageBuilder {
        sendMessage.replyMarkup = inlineKeyboardMarkup()
        return this
    }

    fun enableMarkdown(enable: Boolean): MessageBuilder {
        sendMessage.enableMarkdown(enable)
        return this
    }
    
    fun build(): SendMessage = sendMessage
    
    companion object {
        fun setChatId(chatId: Long): MessageBuilder {
            return MessageBuilder(chatId)
        }
    }
}
