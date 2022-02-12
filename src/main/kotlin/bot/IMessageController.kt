package bot

import entity.MessageType
import org.telegram.telegrambots.meta.api.objects.Update

interface IMessageController {
    
    suspend fun startReceiver(): Boolean

    suspend fun startScheduler(): Boolean

    fun isReceiverStarted(): Boolean

    fun isSchedulerStarted(): Boolean

    fun stopReceiver(): Boolean

    fun stopScheduler(): Boolean

    fun receive(update: Update): Boolean

    fun schedule(chatId: Long, messageType: MessageType): Boolean

    fun send(chatId: Long, messageType: MessageType): Boolean
}
