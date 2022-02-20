package bot

import entity.SendType
import org.telegram.telegrambots.meta.api.objects.Update

interface IMessageController {

    val messageReceiver: IMessageReceiver
    val messageScheduler: IMessageScheduler

    suspend fun startReceiver(): Boolean

    suspend fun startScheduler(): Boolean

    fun isReceiverStarted(): Boolean

    fun isSchedulerStarted(): Boolean

    fun stopReceiver(): Boolean

    fun stopScheduler(): Boolean

    fun receive(update: Update)

    fun schedule(chatId: Long, sendType: SendType)

    fun send(chatId: Long, sendType: SendType)
}
