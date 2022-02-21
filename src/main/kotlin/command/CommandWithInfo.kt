package command

data class CommandWithInfo(val chatId: Long, val messageId: Int, val parsedCommand: ParsedCommand)
