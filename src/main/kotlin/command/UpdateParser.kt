package command

import data.IRequestData
import data.RequestData
import org.telegram.telegrambots.meta.api.objects.Update
import res.Params
import utils.*

class UpdateParser() {

    fun parse(update: Update): IRequestData {
        return RequestData.builder()
            .chatId(update.getChatId)
            .messageId(update.getMessageId)
            .userId(update.getUserId)
            .firstName(update.getFirstName)
            .lastName(update.getLastName)
            .userName(update.getUserName)
            .command(toCommand(update.getText))
            .build()
    }

    private fun toCommand(text: String): Command {

        val trimText = text.trim()
        if (trimText.isEmpty()) return Command.None

        val commandAndText = getDelimitedCommand(trimText)
        return if (isCommand(commandAndText.first)) {
            if (isCommandForBot(commandAndText.first, Params.botName)) {
                val commandText = cutCommandFromText(commandAndText.first)
                getCommandFromText(commandText, commandAndText.second)
            } else {
                Command.NotForMe
            }
        } else {
            Command.getCommand(commandAndText.first, commandAndText.second ?: "")
        }
    }

    private fun getDelimitedCommand(commandText: String): Pair<String, String> {
        val trimmedCommand = commandText.trim()
        var delimitedCommand = Pair(trimmedCommand, "")

        takeIf { trimmedCommand.contains(" ") }
            ?.run {
                val spaceIndex = trimmedCommand.indexOf(" ")
                delimitedCommand = Pair(
                    trimmedCommand.substring(0, spaceIndex),
                    trimmedCommand.substring(spaceIndex + 1)
                )
            }
        return delimitedCommand
    }

    private fun isCommand(text: String): Boolean = text.startsWith(CMD_PREFIX)

    private fun isCommandForBot(command: String, botName: String): Boolean {
        return if (command.contains(DELIMITER_CMD_BOT_NAME)) {
            val commandBotName = command.substring(command.indexOf(DELIMITER_CMD_BOT_NAME) + 1)
            commandBotName == botName
        } else true
    }

    private fun cutCommandFromText(text: String): String {
        return if (text.contains(DELIMITER_CMD_BOT_NAME)) {
            text.substring(1, text.indexOf(DELIMITER_CMD_BOT_NAME))
        } else {
            text.substring(1)
        }
    }

    private fun getCommandFromText(
        commandText: String,
        dataText: String? = null
    ): Command = Command.getCommand(commandText, dataText ?: "")

    companion object {
        const val CMD_PREFIX = "/"
        const val DELIMITER_CMD_BOT_NAME = "@"
    }
}
