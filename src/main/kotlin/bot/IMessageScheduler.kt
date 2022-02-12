package bot

import entity.MessageType

interface IMessageScheduler {

    suspend fun start()

    fun add(chatId: Long, messageType: MessageType)

    fun isSenderStarted(): Boolean

    fun stop(): Boolean

    fun scheduleMessage(chatId: Long, messageType: MessageType)
}
