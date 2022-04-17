package bot

import data.IResponseData
import org.slf4j.LoggerFactory

class MessageSender(private val bot: Bot) : IMessageSender {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun send(responseData: IResponseData) {
        log.info("Send message for ${responseData.chatId}: $responseData ")
        bot.sendTimed(responseData.chatId, responseData)
    }

//    fun sendMessage(chatId: Long, messageText: String) {
//        val message = MessageBuilder.setChatId(chatId)
//            .setText(messageText)
//            .build()
//        send(UserMessage(chatId, message))
//    }
//
//    fun sendMessageAndEdit(chatId: Long, messageId: Int, messageText: String, editMessageText: String) {
//        sendMessage(chatId, messageText)
//        editMessage(chatId, messageId, editMessageText)
//    }
//
//    fun editMessage(chatId: Long, messageId: Int, editMessageText: String = "") {
//        val editMessage = EditMessageBuilder.chatAndMessageId(chatId, messageId)
//            .newMessage(editMessageText)
//            .build()
//        send(EditUserMessage(chatId, editMessage))
//    }
//
//    fun sendAndDelete(chatId: Long, messageId: Int, messageText: String) {
//        sendMessage(chatId, messageText)
//        deleteMessage(chatId, messageId)
//    }
//
//    fun deleteMessage(chatId: Long, messageId: Int) {
//        val deleteMessage = DeleteMessageBuilder.chatAndMessageId(chatId, messageId)
//            .build()
//        send(DeleteUserMessage(chatId, deleteMessage))
//    }
}
