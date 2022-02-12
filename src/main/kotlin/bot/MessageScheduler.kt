package bot

import entity.MessageType
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import res.Params
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class MessageScheduler(private val controller: IMessageController) {

    private val timedMessageQueue: Queue<Pair<Long, MessageType>> = ConcurrentLinkedQueue()
    private val timerQueue = Timer(true)
    private val log = LoggerFactory.getLogger(this::class.java)
    private var isRunning = true

    suspend fun start() {
        log.info("[START] Message scheduler started. Sender.class: ${javaClass.simpleName}")
        isRunning = true
        while (isRunning) {
            var messageTypePair = timedMessageQueue.poll()
            while (messageTypePair != null) {
                scheduleMessage(messageTypePair.first, messageTypePair.second)
                messageTypePair = timedMessageQueue.poll()
            }
            try {
                delay(SLEEP_TIME)
            } catch (e: InterruptedException) {
                log.error("[ERROR] Thread was interrupted. Failure: ", e)
            }
        }
    }

    fun add(chatId: Long, messageType: MessageType) {
        timedMessageQueue.add(Pair(chatId, messageType))
    }

    fun isSenderStarted(): Boolean = isRunning

    fun stop(): Boolean {
        return if (!isRunning) {
            log.info("[WARN] Message scheduler already stopped.")
            false
        } else {
            isRunning = false
            log.info("[STOP] Message scheduler stopped.")
            true
        }
    }

    private fun scheduleMessage(chatId: Long, messageType: MessageType) {
        if (!messageType.notified) {
            permanentMessage(chatId, messageType)
        } else {
            log.info("New message postponed: ${messageType.javaClass.simpleName}")
            timerQueue.schedule(ScheduledMessageTask(chatId, messageType), Params.testTimer)
        }
    }

    private fun permanentMessage(chatId: Long, messageType: MessageType) {
        log.info("New message for sending: ${messageType.javaClass.simpleName}")
        controller.send(chatId, messageType)
    }

    inner class ScheduledMessageTask(private val chatId: Long, private val messageType: MessageType) : TimerTask() {
        override fun run() {
            permanentMessage(chatId, messageType)
        }
    }

    companion object {
        const val SLEEP_TIME: Long = 1000
    }
}
