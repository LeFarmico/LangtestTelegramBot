package data

import command.Command

interface IRequestData {
    val chatId: Long
    val messageId: Int
    val userId: String
    val userName: String
    val firstName: String
    val lastName: String
    val command: Command
}
