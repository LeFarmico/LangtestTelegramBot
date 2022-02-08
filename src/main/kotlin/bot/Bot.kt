package bot

import entity.MessageType
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.PriorityBlockingQueue

class Bot(
    private val botName: String,
    private val botToken: String
) : TelegramLongPollingBot() {

    val lock = Object()

    val sendQueue: Queue<MessageType> = PriorityBlockingQueue()
    val receiveQueue: Queue<Update> = ConcurrentLinkedQueue()

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getBotToken(): String = botToken

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update?) {
        log.info("Receive new Update. updateId: ${update?.updateId ?: "Invalid id"}.")
        receiveQueue.add(update)
    }

//    fun addToSendQueue(messageType: MessageType) {
//        synchronized(lock) {
//            lock.notify()
//            sendQueue.add(messageType)
//        }
//    }
//
//    fun pollFromSendQueue(): MessageType? {
//        synchronized(lock) {
//            sendQueue.element()
//
//        }
//    }

    fun connect() {
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)

        try {
            telegramBotsApi.registerBot(this)
            log.info("Telegram bot connected. Looking for messages")
        } catch (e: TelegramApiRequestException) {
            log.warn("Can't connect. Pause for ${RECONNECT_PAUSE / 100} sec. \n Error: ${e.message}")
            try {
                Thread.sleep(RECONNECT_PAUSE)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            connect()
        }
    }

    companion object {
        /**
         * Reconnect pause in millis
         */
        const val RECONNECT_PAUSE: Long = 10000
    }
}
