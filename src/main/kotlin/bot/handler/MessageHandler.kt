package handler

import command.Command
import command.CommandRequestData
import command.UpdateParser
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import res.Params
import utils.getChatId

class MessageHandler(private val update: Update) {

    private val botName = Params.botName
    private val parser = UpdateParser(update)
    
    private val log = LoggerFactory.getLogger(this::class.java)

    fun handle(command: (CommandRequestData) -> Unit) {
        command(getCommandWithInfo())
    }

    private fun getDelimitedCommand(): Command = parser.parse()

    private fun getCommandWithInfo(): CommandRequestData {
        val chatId = update.getChatId
        val messageId = update.message.messageId
        return CommandRequestData(chatId, messageId, getDelimitedCommand())
    }
    private fun markdownMessageBuilder(chatId: Long, message: String): SendMessage {
        return SendMessage().apply {
            enableMarkdown(true)
            setChatId(chatId.toString())
            text = message
        }
    }
}
