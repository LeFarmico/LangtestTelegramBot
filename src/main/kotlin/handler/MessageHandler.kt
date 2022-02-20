package handler

import ability.langTestAbility.TestAnswerData
import command.Command
import command.CommandParser
import command.ParsedCommand
import entity.AbilityActionType
import entity.SendType
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

    fun getMessageType(): SendType {
        val chatId = update.getChatId
        return if (update.message != null) {
            log.info("[START] Trying to get Message type of: ${update.message.text}")
            val parsedCommand = parser.toParsedCommand(update.message.text, botName)
            getCallbackByCommand(parsedCommand, chatId)
        } else {
            log.info("[START] Trying to get Message type of: ${update.callbackQuery.data}")
            val query = AbilityActionType.getQueryType(update.callbackQuery.data)
            getCallbackByQuery(query, chatId, update.callbackQuery.message.messageId.toInt())
        }
    }

    private fun getCallbackByCommand(command: ParsedCommand, chatId: Long): SendType {
        return when (command.command) {
            Command.Help -> SendType.SendMessage(
                markdownMessageBuilder(chatId, SystemMessages.helpMsg)
            )
            Command.Id -> SendType.SendMessage(
                markdownMessageBuilder(chatId, update.message.from.id.toString())
            )
            Command.Start -> SendType.TimedMsg(
                true,
                markdownMessageBuilder(chatId, SystemMessages.startMsg)
            )
            Command.BeginTest -> SendType.LangTest.Start(chatId)
            Command.None -> SendType.Empty
            Command.NotForMe -> SendType.Empty
            else -> SendType.Error(IllegalStateException("Can't parse command. Try to check Parser settings."))
        }
    }

    private fun getCallbackByQuery(abilityActionType: AbilityActionType, chatId: Long, messageId: Int): SendType {
        return when (abilityActionType) {
            AbilityActionType.None -> SendType.Empty
            AbilityActionType.LangTestQuery.Finish -> SendType.LangTest.Finish(chatId)
            AbilityActionType.LangTestQuery.Right -> SendType.LangTest.Answer(chatId, TestAnswerData(true, messageId))
            AbilityActionType.LangTestQuery.Wrong -> SendType.LangTest.Answer(chatId, TestAnswerData(false, messageId))
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
