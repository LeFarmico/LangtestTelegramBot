package handler

import command.Command
import command.CommandParser
import entity.MessageType
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import res.Params
import res.SystemMessages
import utils.getChatId
import java.lang.IllegalStateException

class MessageHandled(private val update: Update) {

    private val botName = Params.botName
    private val parser = CommandParser()
    private val log = LoggerFactory.getLogger(this::class.java)

    fun getMessageType(): MessageType {
        val parsedCommand = parser.toParsedCommand(update.message.text, botName)
        val chatId = update.getChatId
        log.info("[START] Trying to get Message type of: ${update.message.text}")
        return when (parsedCommand.command) {
            Command.Help -> MessageType.SendMessage(
                markdownMessageBuilder(chatId, SystemMessages.helpMsg)
            )
            Command.Id -> MessageType.SendMessage(
                markdownMessageBuilder(chatId, update.message.from.id.toString())
            )
            Command.Start -> MessageType.TimedMsg(
                true,
                markdownMessageBuilder(chatId, SystemMessages.startMsg)
            )
            Command.None -> MessageType.Empty
            Command.NotForMe -> MessageType.Empty
            else -> MessageType.Error(IllegalStateException("Can't parse command. Try to check Parser settings."))
        }
    }

    private fun markdownMessageBuilder(chatId: Long, message: String): SendMessage {
        return SendMessage().apply {
            enableMarkdown(true)
            setChatId(chatId.toString())
            text = message
        }
    }
}
