package entity

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message

@Suppress("UNCHECKED_CAST")
sealed class CallbackType(val notified: Boolean = false) {

    data class SendMessage(val message: BotApiMethod<Message>) : CallbackType()
    data class TimedMsg(val isNotified: Boolean, val message: BotApiMethod<Message>) : CallbackType(isNotified)
    data class Error(val exception: Exception) : CallbackType(false)
    object Empty : CallbackType(false)

    sealed class LangTest : CallbackType() {
        data class Start(val chatId: Long) : LangTest()
        data class Answer(val chatId: Long, val isCorrect: Boolean) : LangTest()
        data class Finish(val chatId: Long) : LangTest()
    }
}
