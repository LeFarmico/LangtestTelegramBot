package bot

import entity.DeleteUserMessage
import entity.EditUserMessage
import entity.SendData
import entity.UserMessage
import messageBuilders.DeleteMessageBuilder
import messageBuilders.EditMessageBuilder
import messageBuilders.MessageBuilder
import org.slf4j.LoggerFactory

class MessageSender(private val bot: Bot) : IMessageSender {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun send(sendData: SendData) {
        log.info("Send message for ${sendData.chatId}: $sendData ")
        bot.sendTimed(sendData.chatId, sendData)
    }

    fun sendMessage(chatId: Long, messageText: String) {
        val message = MessageBuilder.setChatId(chatId)
            .setText(messageText)
            .build()
        send(UserMessage(chatId, message))
    }

    fun sendMessageAndEdit(chatId: Long, messageId: Int, messageText: String, editMessageText: String) {
        sendMessage(chatId, messageText)
        editMessage(chatId, messageId, editMessageText)
    }

    fun editMessage(chatId: Long, messageId: Int, editMessageText: String = "") {
        val editMessage = EditMessageBuilder.chatAndMessageId(chatId, messageId)
            .newMessage(editMessageText)
            .build()
        send(EditUserMessage(chatId, editMessage))
    }

    fun sendAndDelete(chatId: Long, messageId: Int, messageText: String) {
        sendMessage(chatId, messageText)
        deleteMessage(chatId, messageId)
    }

    fun deleteMessage(chatId: Long, messageId: Int) {
        val deleteMessage = DeleteMessageBuilder.chatAndMessageId(chatId, messageId)
            .build()
        send(DeleteUserMessage(chatId, deleteMessage))
    }
}
