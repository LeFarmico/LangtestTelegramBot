package bot

import entity.SendData
import org.telegram.telegrambots.meta.api.objects.Update

interface IMessageController {

    val messageReceiver: IMessageReceiver
    val messageScheduler: IMessageSender

    suspend fun startReceiver(): Boolean

    suspend fun startScheduler(): Boolean

    fun isReceiverStarted(): Boolean

    fun isSchedulerStarted(): Boolean

    fun stopReceiver(): Boolean

    fun stopScheduler(): Boolean

    fun receive(update: Update)

    fun schedule(chatId: Long, sendData: SendData)

    fun send(chatId: Long, sendData: SendData)
}
