package bot

import entity.MessageType
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

class MessageController(private val bot: Bot) : IMessageController {

    override val messageReceiver: IMessageReceiver = MessageReceiver(this)
    override val messageScheduler: MessageScheduler = MessageScheduler(this)

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
            messageScheduler.start()
            true
        } catch (e: InterruptedException) {
            log.error("[ERROR] Message sender thread was interrupted. Failure: ", e)
            false
        }
    }

    override fun isReceiverStarted(): Boolean = messageReceiver.isReceiverStarted()

    override fun isSchedulerStarted(): Boolean = messageScheduler.isSenderStarted()

    override fun stopReceiver(): Boolean = messageReceiver.stop()

    override fun stopScheduler(): Boolean = messageScheduler.stop()

    override fun receive(update: Update): Boolean {
        return if (isReceiverStarted()) {
            messageReceiver.add(update)
            true
        } else {
            log.warn("[WARN] Receiver is not started")
            false
        }
    }

    override fun schedule(chatId: Long, messageType: MessageType): Boolean {
        return if (isSchedulerStarted()) {
            messageScheduler.add(chatId, messageType)
            true
        } else {
            log.warn("[WARN] Sender is not started")
            false
        }
    }

    override fun send(chatId: Long, messageType: MessageType): Boolean {
        return try {
            bot.sendTimed(chatId, messageType)
            true
        } catch (e: TelegramApiException) {
            log.error("[ERROR] Can't send message: ", e)
            false
        }
    }
}
