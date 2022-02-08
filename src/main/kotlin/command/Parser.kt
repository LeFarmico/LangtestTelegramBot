package command

class Parser(private val botName: String) {

    fun getParsedCommand(text: String): ParsedCommand {
        val trimText = text.trim()
        val noneCommand = ParsedCommand(Command.None, trimText)

        if (trimText.isEmpty()) return noneCommand
        val commandAndText = getDelimitedCommand(trimText)

        if (isCommand(commandAndText.first)) {
            return if (isCommandForBot(commandAndText.first)) {
                val commandText = cutCommandFromText(commandAndText.first)
                val command = getCommandFromText(commandText)

                ParsedCommand(command, commandAndText.second)
            } else {
                ParsedCommand(Command.NotForMe, commandAndText.second)
            }
        }
        return noneCommand
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

    private fun isCommandForBot(command: String): Boolean {
        return if (command.contains(DELIMITER_CMD_BOT_NAME)) {
            val botName = command.substring(command.indexOf(DELIMITER_CMD_BOT_NAME) + 1)
            botName == this.botName
        } else true
    }

    private fun cutCommandFromText(text: String): String {
        return if (text.contains(DELIMITER_CMD_BOT_NAME)) {
            text.substring(1, text.indexOf(DELIMITER_CMD_BOT_NAME))
        } else {
            text.substring(1)
        }
    }

    private fun getCommandFromText(text: String): Command = Command.getCommand(text)

    companion object {
        const val CMD_PREFIX = "/"
        const val DELIMITER_CMD_BOT_NAME = "@"
    }
}
