package command

data class CommandRequestData(val chatId: Long, val messageId: Int, val delimitedCommand: DelimitedCommand)
