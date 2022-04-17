package data

import org.telegram.telegrambots.meta.api.methods.BotApiMethod

sealed interface IResponseData {
    val chatId: Long
    val message: BotApiMethod<out java.io.Serializable>
}
