package handler

import command.ParsedCommand
import entity.MessageType
import org.telegram.telegrambots.meta.api.objects.Update

abstract class AbstractHandler {

    abstract fun operate(chatId: String, parsedCommand: ParsedCommand, update: Update): MessageType
}
