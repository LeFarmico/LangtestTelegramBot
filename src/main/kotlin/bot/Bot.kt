package bot

import ability.AbilityManager
import ability.IAbilityManager
import ability.langTestAbility.LangTestAbility
import command.CommandManager
import entity.*
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

    private val messageSender = MessageSender(this)
    override val abilityManager: IAbilityManager = AbilityManager()
    private val commandManager = CommandManager(abilityManager, messageSender)
    private val messageReceiver = MessageReceiver(commandManager)
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getBotToken(): String = botToken

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update?) {
        CoroutineScope(Dispatchers.IO).launch {
            log.info("Receive new Update. updateId: ${update?.updateId ?: "Invalid id"}.")
            try {
                messageReceiver.add(update!!)
            } catch (e: NullPointerException) {
                log.error("[ERROR] receive on null object")
            }
        }
    }

    override suspend fun connect() {
        val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
        log.info("[LOADING] Bot connecting")
        try {
            telegramBotsApi.registerBot(this)
            log.info("[STARTED] Telegram bot connected. Looking for messages")
            abilityManager.addAbility(LangTestAbility::class.java, LangTestAbility(messageSender))
        } catch (e: TelegramApiRequestException) {
            log.warn("Can't connect. Pause for ${RECONNECT_PAUSE / 100} sec. \n Error: ${e.message}")

            // Waiting until next reconnect
            delay(SLEEP_TIME)
            // Reconnect.
            connect()
        }

        CoroutineScope(Dispatchers.Default).launch {
            launch { messageReceiver.start() }
        }
    }

    override fun sendMessageCallback(chatId: Long?, request: Any?) {
        if (request !is SendData) {
            log.error("Execute error. Illegal type of message: ${request!!.javaClass.simpleName}", IllegalArgumentException())
            return
        }
        when (request) {
            is EditUserMessage -> execute(request.message)
            Empty -> { log.info("[INFO] request is not identified: ${request.javaClass}") }
            is UserMessage -> execute(request.message)
            is DeleteUserMessage -> execute(request.deleteMessage)
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
