package bot

import command.Command
import command.CommandParser
import entity.MessageType
import handler.AbstractHandler
import handler.SystemHandler
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
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class Bot(
    private val botName: String,
    private val botToken: String
) : TimedSendLongPollingBot() {

    private val receiveQueue: Queue<Update> = ConcurrentLinkedQueue()
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun getBotToken(): String = botToken

    override fun getBotUsername(): String = botName

    override fun onUpdateReceived(update: Update?) {
        log.info("Receive new Update. updateId: ${update?.updateId ?: "Invalid id"}.")
        receiveQueue.add(update)
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
            MessageReceiver().run()
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
        }
    }

    private inner class MessageReceiver {

        private val log = LoggerFactory.getLogger(this::class.java)

        suspend fun run() {
            log.info("[STARTED] MsgReceiver. Receiver.class: ${javaClass.simpleName}")
            while (true) {
                var update = receiveQueue.poll()
                while (update != null) {
                    log.info("New object for analyze in queue: ${update.javaClass.simpleName}")
                    analyze(update)
                    update = receiveQueue.poll()
                }
                try {
                    delay(SLEEP_TIME)
                } catch (e: InterruptedException) {
                    log.error("Catch interrupt. Exit.", e)
                }
            }
        }

        private fun analyze(update: Update) {
            log.info("Update receiver: $update")
            val msg = update.message
            val parsedCommand = CommandParser().toParsedCommand(msg.text ?: update.callbackQuery.data, botName)
            val chatId = update.message.chatId ?: update.callbackQuery.message.chatId

            val handler = getHandler(parsedCommand.command)
            val operationResult = handler.operate(chatId.toString(), parsedCommand, update)

            sendTimed(chatId, operationResult)
        }

        private fun getHandler(command: Command): AbstractHandler {
            return when (command) {
                Command.Start -> SystemHandler()
                Command.Help -> SystemHandler()
                Command.Id -> SystemHandler()
                Command.None -> SystemHandler()
                Command.NotForMe -> SystemHandler()

                Command.BeginTest -> TODO()
                Command.RightAnswer -> TODO()
                Command.WrongAnswer -> TODO()
                Command.WordsInDictionary -> TODO()
                Command.AddWord -> TODO()
                Command.TimeToNextTest -> TODO()
            }
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
