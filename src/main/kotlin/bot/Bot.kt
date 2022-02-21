package bot

import ability.AbilityManager
import ability.IAbilityManager
import ability.langTestAbility.LangTestAbility
import entity.SendData
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
    override val abilityManager: IAbilityManager = AbilityManager()
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

    override fun sendMessageCallback(chatId: Long?, request: Any?) {
        if (request !is SendData) {
            log.error("Execute error. Illegal type of message: ${request!!.javaClass.simpleName}", IllegalArgumentException())
            return
        }
        when (request) {
            is SendData.SendMessage -> {
                log.info("Use execute for: ${request.message.javaClass.simpleName}")
                execute(request.message)
            }
            is SendData.TimedMsg -> {
                log.info("Use execute for timed command: ${request.message.javaClass.simpleName}")
                execute(request.message)
            }
            is SendData.Error -> log.error("[ERROR] request error.", request.exception)
            SendData.Empty -> { log.info("[INFO] request is not identified: ${request.javaClass}") }
            is SendData.LangTest -> langTestExecute(request)
            is SendData.EditMessage -> execute(request.message)
        }
    }

    private fun langTestExecute(request: SendData.LangTest) {
        log.info("Use execute for LangTest command: ${request.javaClass.simpleName}")
        when (request) {
            is SendData.LangTest.Start -> {
                abilityManager.addAndStartAbility(
                    request.chatId,
                    LangTestAbility(controller, request.chatId)
                )
            }
            is SendData.LangTest.Answer -> abilityManager.abilityAction(request.chatId, request.testAnswerData)
            is SendData.LangTest.Finish -> abilityManager.finishAbility(request.chatId)
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
