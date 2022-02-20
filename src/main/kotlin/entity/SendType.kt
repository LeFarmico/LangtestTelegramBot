package entity

import ability.langTestAbility.TestAnswerData
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
sealed class SendType(val notified: Boolean = false) {

    data class SendMessage(val message: BotApiMethod<Message>) : SendType()
    data class EditMessage(val message: BotApiMethod<Serializable>) : SendType()
    data class TimedMsg(val isNotified: Boolean, val message: BotApiMethod<Message>) : SendType(isNotified)
    data class Error(val exception: Exception) : SendType(false)
    object Empty : SendType(false)

    sealed class LangTest : SendType() {
        data class Start(val chatId: Long) : LangTest()
        data class Answer(val chatId: Long, val testAnswerData: TestAnswerData) : LangTest()
        data class Finish(val chatId: Long) : LangTest()
    }
}
