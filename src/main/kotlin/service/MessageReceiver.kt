package service

import bot.Bot
import command.Command
import command.Parser
import handler.AbstractHandler
import handler.SystemHandler
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update

class MessageReceiver(private val bot: Bot) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val parser: Parser = Parser(bot.botUsername)

    fun run() {
        log.info("[STARTED] MsgReceiver. Bot.class: ${bot.javaClass.simpleName}")

        while (true) {
            var update = bot.receiveQueue.poll()
            while (update != null) {
                log.info("New object for analyze in queue: ${update.javaClass.simpleName}")
                analyze(update)
                update = bot.receiveQueue.poll()
            }
            try {
                Thread.sleep(SLEEP_TIME)
            } catch (e: InterruptedException) {
                log.error("Catch interrupt. Exit.", e)
            }
        }
    }

    private fun analyze(update: Update) {
        log.info("Update receiver: $update")
        analyzeForUpdateType(update)
    }

    private fun analyzeForUpdateType(update: Update) {
        val msg = update.message
        val parsedCommand = parser.getParsedCommand(msg.text ?: update.callbackQuery.data)
        val chatId = update.message.chatId ?: update.callbackQuery.message.chatId

        val handler = getHandler(parsedCommand.command)
        val operationResult = handler.operate(chatId.toString(), parsedCommand, update)

        bot.sendQueue.add(operationResult)
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

    companion object {
        const val SLEEP_TIME: Long = 1000
    }
}
