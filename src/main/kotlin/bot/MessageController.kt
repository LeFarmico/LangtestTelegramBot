package bot

import entity.SendData
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class MessageController(private val bot: Bot) : IMessageController {

    override val messageReceiver: IMessageReceiver = MessageReceiver(this)
    val messageSender: IMessageSender = MessageSender(this)

    private val log = LoggerFactory.getLogger(javaClass.simpleName)

    override suspend fun startReceiver(): Boolean {
        return try {
            messageReceiver.start()
            true
        } catch (e: InterruptedException) {
            log.error("[ERROR] Message receiver thread was interrupted. Failure: ", e)
            false
        }
    }

    override suspend fun startScheduler(): Boolean {
        return try {
            messageSender.start()
            true
        } catch (e: InterruptedException) {
            log.error("[ERROR] Message sender thread was interrupted. Failure: ", e)
            false
        }
    }

    override fun isReceiverStarted(): Boolean = messageReceiver.isReceiverStarted()

    override fun isSchedulerStarted(): Boolean = messageSender.isSenderStarted()

    override fun stopReceiver(): Boolean = messageReceiver.stop()

    override fun stopScheduler(): Boolean = messageSender.stop()

    override fun receive(update: Update) {
        if (isReceiverStarted()) {
            messageReceiver.add(update)
        } else {
            log.warn("[WARN] Receiver is not started")
        }
    }

    override fun schedule(chatId: Long, sendData: SendData) {
        if (isSchedulerStarted()) {
            messageSender.add(chatId, sendData)
        } else {
            log.warn("[WARN] Sender is not started")
        }
    }

    override fun send(chatId: Long, sendData: SendData) {
        try {
            bot.sendTimed(chatId, sendData)
        } catch (e: TelegramApiException) {
            log.error("[ERROR] Can't send message: ", e)
        }
    }
}
