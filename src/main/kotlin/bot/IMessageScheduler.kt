package bot

import entity.CallbackType

interface IMessageScheduler {

    suspend fun start()

    fun add(chatId: Long, callbackType: CallbackType)

    fun isSenderStarted(): Boolean

    fun stop(): Boolean

    fun scheduleMessage(chatId: Long, callbackType: CallbackType)
}
