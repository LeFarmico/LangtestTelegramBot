package entity

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message
import java.io.Serializable

sealed interface SendData {
    val chatId: Long?
}

data class UserMessage(override val chatId: Long, val message: BotApiMethod<Message>) : SendData
data class EditUserMessage(override val chatId: Long, val message: BotApiMethod<Serializable>) : SendData
data class DeleteUserMessage(override val chatId: Long, val deleteMessage: BotApiMethod<Boolean>) : SendData
object Empty : SendData { override val chatId: Long? = null }
