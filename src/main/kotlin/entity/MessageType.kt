package entity

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendSticker
import org.telegram.telegrambots.meta.api.objects.Message

@Suppress("UNCHECKED_CAST")
sealed class MessageType(val notified: Boolean = false) {

    data class SendMessage(val message: BotApiMethod<Message>) : MessageType()
    data class TimedMsg(val isNotified: Boolean, val message: BotApiMethod<Message>) : MessageType(isNotified)
    data class Sticker(val sticker: SendSticker) : MessageType()
    object Empty : MessageType(false)
    object Error : MessageType(false)
}
