package handler

import command.CommandParser
import command.CommandWithInfo
import command.ParsedCommand
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import res.Params
import utils.getChatId

class MessageHandler(private val update: Update) {

    private val botName = Params.botName
    private val parser = CommandParser()
    private val log = LoggerFactory.getLogger(this::class.java)

    fun handle(command: (CommandWithInfo) -> Unit) {
        command(getCommandWithInfo())
    }

    private fun getParsedCommand(): ParsedCommand = parser.toParsedCommand(update.message.text, botName)

    private fun getCommandWithInfo(): CommandWithInfo {
        val chatId = update.getChatId
        val messageId = update.message.messageId
        return CommandWithInfo(chatId, messageId, getParsedCommand())
    }
    private fun markdownMessageBuilder(chatId: Long, message: String): SendMessage {
        return SendMessage().apply {
            enableMarkdown(true)
            setChatId(chatId.toString())
            text = message
        }
    }
}
