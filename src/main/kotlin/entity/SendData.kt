package entity

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message
import java.io.Serializable

sealed interface SendData

data class UserMessage(val message: BotApiMethod<Message>) : SendData
data class EditMessage(val message: BotApiMethod<Serializable>) : SendData
object Empty : SendData
