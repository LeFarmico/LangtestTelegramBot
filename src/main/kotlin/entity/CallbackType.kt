package entity

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

@Suppress("UNCHECKED_CAST")
sealed class CallbackType(val notified: Boolean = false) {

    data class SendMessage(val message: BotApiMethod<Message>) : CallbackType()
    data class TimedMsg(val isNotified: Boolean, val message: BotApiMethod<Message>) : CallbackType(isNotified)
    data class StartTest(val chatId: Long) : CallbackType()
    data class Next(val chatId: Long) : CallbackType()
    data class Error(val exception: Exception) : CallbackType(false)
    object Empty : CallbackType(false)
}
