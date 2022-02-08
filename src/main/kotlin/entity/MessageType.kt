package entity

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendSticker
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.LocalDateTime

@Suppress("UNCHECKED_CAST")
sealed class MessageType(val notifyTime: LocalDateTime? = null) : Comparable<MessageType> {

    override fun compareTo(other: MessageType): Int {
        return if (
            this.notifyTime == null ||
            this.notifyTime < other.notifyTime
        ) {
            1
        } else if (
            other.notifyTime == null ||
            this.notifyTime > other.notifyTime
        ) {
            -1
        } else {
            0
        }
    }

    data class SendMessage(val message: BotApiMethod<Message>) : MessageType()
    data class Sticker(val sticker: SendSticker) : MessageType()
    object Empty : MessageType()
    object Error : MessageType()
}
