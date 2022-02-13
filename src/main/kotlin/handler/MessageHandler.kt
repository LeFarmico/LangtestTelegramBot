package handler

import command.Command
import command.CommandParser
import command.ParsedCommand
import command.QueryType
import entity.CallbackType
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import res.Params
import res.SystemMessages
import utils.getChatId
import java.lang.IllegalStateException

class MessageHandler(private val update: Update) {

    private val botName = Params.botName
    private val parser = CommandParser()
    private val log = LoggerFactory.getLogger(this::class.java)

    fun getMessageType(): CallbackType {
        val chatId = update.getChatId
        return if (update.message != null) {
            log.info("[START] Trying to get Message type of: ${update.message.text}")
            val parsedCommand = parser.toParsedCommand(update.message.text, botName)
            getCallbackByCommand(parsedCommand, chatId)
        } else {
            log.info("[START] Trying to get Message type of: ${update.callbackQuery.data}")
            val query = QueryType.getQueryType(update.callbackQuery.data)
            getCallbackByQuery(query, chatId)
        }
    }

    private fun getCallbackByCommand(command: ParsedCommand, chatId: Long): CallbackType {
        return when (command.command) {
            Command.Help -> CallbackType.SendMessage(
                markdownMessageBuilder(chatId, SystemMessages.helpMsg)
            )
            Command.Id -> CallbackType.SendMessage(
                markdownMessageBuilder(chatId, update.message.from.id.toString())
            )
            Command.Start -> CallbackType.TimedMsg(
                true,
                markdownMessageBuilder(chatId, SystemMessages.startMsg)
            )
            Command.BeginTest -> CallbackType.StartTest(chatId)
            Command.None -> CallbackType.Empty
            Command.NotForMe -> CallbackType.Empty
            else -> CallbackType.Error(IllegalStateException("Can't parse command. Try to check Parser settings."))
        }
    }

    private fun getCallbackByQuery(queryType: QueryType, chatId: Long): CallbackType {
        return when (queryType) {
            QueryType.Right -> CallbackType.Next(chatId)
            QueryType.Finish -> TODO()
            QueryType.None -> TODO()
            QueryType.Wrong -> TODO()
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
