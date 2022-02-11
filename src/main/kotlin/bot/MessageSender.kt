package bot

import entity.MessageType
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import res.Params
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class MessageSender(private val bot: Bot) {

    private val timedMessageQueue: Queue<Pair<Long, MessageType>> = ConcurrentLinkedQueue()
    private val timerQueue = Timer(true)
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun run() {
        while (true) {
            var messageTypePair = timedMessageQueue.poll()
            while (messageTypePair != null) {
                scheduleMessage(messageTypePair.first, messageTypePair.second)
                messageTypePair = timedMessageQueue.poll()
            }
            try {
                delay(SLEEP_TIME)
            } catch (e: InterruptedException) {
                log.error("Catch interrupt. Exit.", e)
            }
        }
    }

    fun add(chatId: Long, messageType: MessageType) {
        timedMessageQueue.add(Pair(chatId, messageType))
    }

    private fun scheduleMessage(chatId: Long, messageType: MessageType) {
        if (!messageType.notified) {
            permanentMessage(chatId, messageType)
        } else {
            timerQueue.schedule(ScheduledMessageTask(chatId, messageType), Params.testTimer)
        }
    }

    private fun permanentMessage(chatId: Long, messageType: MessageType) {
        bot.sendTimed(chatId, messageType)
    }

    inner class ScheduledMessageTask(private val chatId: Long, private val messageType: MessageType) : TimerTask() {
        override fun run() {
            bot.sendTimed(chatId, messageType)
        }
    }

    companion object {
        const val SLEEP_TIME: Long = 1000
    }
}
