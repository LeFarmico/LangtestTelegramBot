package bot

import entity.MessageType
import handler.MessageHandled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.extensions.bots.timedbot.TimedSendLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import utils.getChatId
import java.lang.NullPointerException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class Bot(
    private val botName: String,
    private val botToken: String
) : TimedSendLongPollingBot() {

    private val receiver = MessageReceiver()
    private val sender = MessageSender(this)
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getBotToken(): String = botToken

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update?) {
        CoroutineScope(Dispatchers.IO).launch {
            log.info("Receive new Update. updateId: ${update?.updateId ?: "Invalid id"}.")
            try {
                receiver.add(update!!)
            } catch (e: NullPointerException) {
                log.error("[ERROR] receive on null object")
            }
        }
    }

    suspend fun connect() {
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)

        log.info("[STARTED] Bot connecting")
        try {
            telegramBotsApi.registerBot(this)
            log.info("Telegram bot connected. Looking for messages")
        } catch (e: TelegramApiRequestException) {
            log.warn("Can't connect. Pause for ${RECONNECT_PAUSE / 100} sec. \n Error: ${e.message}")

            // Waiting until next reconnect
            delay(SLEEP_TIME)
            // Reconnect.
            connect()
        }

        CoroutineScope(Dispatchers.Default).launch {
            launch { receiver.run() }
            launch { sender.run() }
        }
    }

    override fun sendMessageCallback(chatId: Long?, messageRequest: Any?) {
        if (messageRequest !is MessageType) {
            log.error("Execute error. Illegal type of message: ${messageRequest!!.javaClass.simpleName}", IllegalArgumentException())
            return
        }
        when (messageRequest) {
            is MessageType.SendMessage -> {
                log.info("Use execute for: ${messageRequest.message.javaClass.simpleName}")
                execute(messageRequest.message)
            }
            MessageType.Empty -> TODO()
            MessageType.Error -> TODO()
            is MessageType.Sticker -> TODO()
            is MessageType.TimedMsg -> execute(messageRequest.message)
        }
    }

    private inner class MessageReceiver {

        private val receiveQueue: Queue<Update> = ConcurrentLinkedQueue()
        private val log = LoggerFactory.getLogger(this::class.java)

        suspend fun run() {
            log.info("[STARTED] MsgReceiver. Receiver.class: ${javaClass.simpleName}")
            while (true) {
                var update = receiveQueue.poll()
                while (update != null) {
                    log.info("New object for analyze in queue: ${update.javaClass.simpleName}")
                    handle(update)
                    update = receiveQueue.poll()
                }
                try {
                    delay(SLEEP_TIME)
                } catch (e: InterruptedException) {
                    log.error("Catch interrupt. Exit.", e)
                }
            }
        }

        suspend fun add(update: Update) {
            receiveQueue.add(update)
        }

        private fun handle(update: Update) {
            log.info("Handle receiver: $update")
            val chatId = update.getChatId
            val handler = MessageHandled(update)
            val messageType = handler.getMessageType()
            sender.add(chatId, messageType)
        }
    }

    companion object {
        /**
         * Reconnect pause in millis
         */
        const val RECONNECT_PAUSE: Long = 10000
        const val SLEEP_TIME: Long = 1000
    }
}
