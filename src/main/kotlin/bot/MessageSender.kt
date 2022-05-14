package bot

import data.IResponseData
import org.slf4j.LoggerFactory

class MessageSender(private val bot: Bot) : IMessageSender {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun send(responseData: IResponseData) {
        log.info("Send message for ${responseData.chatId}: $responseData ")
        bot.sendTimed(responseData.chatId, responseData)
    }
}
