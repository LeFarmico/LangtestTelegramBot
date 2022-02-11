package handler

import command.Command
import command.CommandParser
import entity.MessageType
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import res.Params
import utils.getChatId

class MessageHandled(private val update: Update) {

    private val botName = Params.botName
    private val parser = CommandParser()
    private val log = LoggerFactory.getLogger(this::class.java)

    fun getMessageType(): MessageType {
        val parsedCommand = parser.toParsedCommand(update.message.text, botName)
        val chatId = update.getChatId
        return when (parsedCommand.command) {
            Command.Help -> MessageType.SendMessage(
                message(chatId) {
                    it.enableMarkdown(true)
                    StringBuilder().apply {
                        append("*Это вспомогательное сообщение - здесь назодятся всё что я умею.*").append(NEXT_LINE).append(
                            NEXT_LINE
                        )
                        append("[/start](/start) - приветственное сообщение").append(NEXT_LINE)
                        append("[/help](/help) - узнать все что я умею").append(NEXT_LINE)
                        append("[/langtest](/langtest) - запуск теста").append(NEXT_LINE)
                        append("[/timetorepeat](/timetorepeat) - установить время, через которое будет приходить новый тест").append(
                            NEXT_LINE
                        )
                        append("[/wordsintest](/wordsintest) - Установить количество слов в тесте").append(NEXT_LINE)
                        append("[/addword](/addword) - добавить слово").append(NEXT_LINE)
                    }.toString()
                }
            )
            Command.Id -> MessageType.SendMessage(message(chatId) { update.message.from.id.toString() })
            Command.Start -> MessageType.TimedMsg(
                true,
                message(chatId) {
                    it.enableMarkdown(true)
                    StringBuilder().apply {
                        append("Привет я бот Испонского языка").append(NEXT_LINE)
                        append("Я создан чтобы помочь изучать его").append(NEXT_LINE)
                        append("Чтобы узнать что я умею - введи команду [/help](/help)")
                    }.toString()
                }
            )
            Command.None -> MessageType.Empty
            Command.NotForMe -> MessageType.Empty
            else -> MessageType.Error
        }
    }

    private fun message(chatId: Long, message: (SendMessage) -> String): SendMessage {
        return SendMessage().apply {
            setChatId(chatId.toString())
            text = message(this)
        }
    }

    companion object {
        const val NEXT_LINE = "\n"
    }
}
