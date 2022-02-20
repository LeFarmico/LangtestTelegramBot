package bot

import entity.SendType

interface IMessageScheduler {

    suspend fun start()

    fun add(chatId: Long, callbackType: SendType)

    fun isSenderStarted(): Boolean

    fun stop(): Boolean

    fun scheduleMessage(chatId: Long, callbackType: SendType)
}
