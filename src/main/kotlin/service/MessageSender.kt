package service

import bot.Bot
import entity.MessageType
import org.slf4j.LoggerFactory

class MessageSender(private val bot: Bot) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun run() {
        log.info("[STARTED] MsgSender. Bot class: ${bot.javaClass.simpleName}")

        while (true) {
            var update = bot.sendQueue.poll()
            while (update != null) {
                log.info("Trying to send: ${update.javaClass.simpleName}")
                send(update)
                update = bot.sendQueue.poll()
            }
            try {
                Thread.sleep(SENDER_SLEEP_TIME)
            } catch (e: InterruptedException) {
                log.error("Catch interrupt. Exit.", e)
            }
        }
    }

    private fun send(messageType: MessageType) {
        when (messageType) {
            is MessageType.SendMessage -> {
                log.info("Use execute for: ${messageType.message.javaClass.simpleName}")
                bot.execute(messageType.message)
            }
            is MessageType.Sticker -> {
                log.info("Use sendSticker for: ${messageType.sticker.javaClass.simpleName}")
                bot.execute(messageType.sticker)
            }
            MessageType.Error -> {
                log.error("Execute error. Illegal type of message: ${messageType.javaClass.simpleName}", IllegalArgumentException())
            }
            MessageType.Empty -> {}
        }
    }

    companion object {
        const val SENDER_SLEEP_TIME: Long = 1000
    }
}
