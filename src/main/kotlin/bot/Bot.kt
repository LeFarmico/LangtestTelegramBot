package bot

import ability.LangTestAbility
import entity.CallbackType
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

class Bot(
    private val botName: String,
    private val botToken: String
) : TimedSendLongPollingBot(), IBot {

    override val controller: IMessageController = MessageController(this)
    private val langTestAbility = LangTestAbility(controller)
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getBotToken(): String = botToken

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update?) {
        CoroutineScope(Dispatchers.IO).launch {
            log.info("Receive new Update. updateId: ${update?.updateId ?: "Invalid id"}.")
            try {
                controller.receive(update!!)
            } catch (e: NullPointerException) {
                log.error("[ERROR] receive on null object")
            }
        }
    }

    override suspend fun connect() {
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
            launch { controller.startReceiver() }
            launch { controller.startScheduler() }
        }
    }

    override fun sendMessageCallback(chatId: Long?, messageRequest: Any?) {
        if (messageRequest !is CallbackType) {
            log.error("Execute error. Illegal type of message: ${messageRequest!!.javaClass.simpleName}", IllegalArgumentException())
            return
        }
        when (messageRequest) {
            is CallbackType.SendMessage -> {
                log.info("Use execute for: ${messageRequest.message.javaClass.simpleName}")
                execute(messageRequest.message)
            }
            is CallbackType.StartTest -> langTestAbility.start(messageRequest.chatId)
            is CallbackType.TimedMsg -> execute(messageRequest.message)
            is CallbackType.Error -> log.error("[ERROR] request error.", messageRequest.exception)
            CallbackType.Empty -> { log.info("[INFO] request is not identified: ${messageRequest.javaClass}") }
            is CallbackType.Next -> langTestAbility.next()
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
