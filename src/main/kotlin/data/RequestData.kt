package data

import command.Command

data class RequestData(
    override val chatId: Long,
    override val messageId: Int,
    override val userId: String,
    override val userName: String?,
    override val firstName: String,
    override val lastName: String,
    override val command: Command,
) : IRequestData {

    companion object {
        fun builder(): RequestDataBuilder {
            return RequestDataBuilder()
        }
    }
}
