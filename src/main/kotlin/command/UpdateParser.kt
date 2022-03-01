package command

import org.telegram.telegrambots.meta.api.objects.Update
import res.Params

class UpdateParser(private val update: Update) {

    fun parse(): Command {
        return if (update.message != null) {
            toCommand(update.message.text)
        } else if (update.callbackQuery.message != null) {
            CallbackParser(update.callbackQuery.data).toCommand()
        } else {
            Command.None
        }
    }

    private fun toCommand(text: String): Command {

        val trimText = text.trim()

        if (trimText.isEmpty()) return Command.None

        val commandAndText = getDelimitedCommand(trimText)
        if (isCommand(commandAndText.first)) {
            return if (isCommandForBot(commandAndText.first, Params.botName)) {
                val commandText = cutCommandFromText(commandAndText.first)
                getCommandFromText(commandText, commandAndText.second)
            } else {
                Command.NotForMe
            }
        }
        return Command.None
    }

    private fun getDelimitedCommand(commandText: String): Pair<String, String> {
        val trimmedCommand = commandText.trim()
        var delimitedCommand = Pair(trimmedCommand, "")

        takeIf { trimmedCommand.contains(" ") }
            ?.run {
                val spaceIndex = trimmedCommand.indexOf(" ")
                delimitedCommand = Pair(trimmedCommand.substring(0, spaceIndex), trimmedCommand.substring(spaceIndex + 1))
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
