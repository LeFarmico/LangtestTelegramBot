package bot

import data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.extensions.bots.timedbot.TimedSendLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

class Bot(
    private val botName: String,
    private val botToken: String
) : TimedSendLongPollingBot(), IBot {

    private val messageSender = MessageSender(this)
    private val messageReceiver = MessageReceiver(messageSender)
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
            val myCommands = SetMyCommands()
            myCommands.commands = mutableListOf()
            myCommands.commands.clear()
            myCommands.commands.add(BotCommand("/start", "Начать викторину"))
            myCommands.commands.add(BotCommand("/stop", "Удалить данные пользователя и прогресс"))
            myCommands.commands.add(BotCommand("/help", "Сообщение со всеми командами"))
            myCommands.commands.add(BotCommand("/stats", "Данные пользователя"))
            myCommands.commands.add(BotCommand("/timetonexttest", "Время до следующей викторины"))
            myCommands.commands.add(BotCommand("/restartquiz", "Сбросить текущую викторину"))
            execute(myCommands)
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
        if (request !is IResponseData) {
            log.error("Execute error. Illegal type of message: ${request!!.javaClass.simpleName}", IllegalArgumentException())
            return
        }
        when (request) {
            is EditUserMessage -> execute(request.message)
            Empty -> { }
            is DeleteUserMessage -> execute(request.message)
            is UserMessage -> execute(request.message)
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
