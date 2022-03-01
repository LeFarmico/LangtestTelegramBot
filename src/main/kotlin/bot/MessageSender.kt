package bot

import entity.SendData
import org.slf4j.LoggerFactory

class MessageSender(private val bot: Bot) : IMessageSender {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun send(sendData: SendData) {
        log.info("Send message for ${sendData.chatId}: $sendData ")
        bot.sendTimed(sendData.chatId, sendData)
    }
}
