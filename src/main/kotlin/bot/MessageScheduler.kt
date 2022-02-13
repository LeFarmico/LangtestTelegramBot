package bot

import entity.CallbackType
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import res.Params
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class MessageScheduler(private val controller: IMessageController) : IMessageScheduler {

    private val timedMessageQueue: Queue<Pair<Long, CallbackType>> = ConcurrentLinkedQueue()
    private val timerQueue = Timer(true)
    private val log = LoggerFactory.getLogger(this::class.java)
    private var isRunning = true

    override suspend fun start() {
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

    override fun add(chatId: Long, callbackType: CallbackType) {
        timedMessageQueue.add(Pair(chatId, callbackType))
    }

    override fun isSenderStarted(): Boolean = isRunning

    override fun stop(): Boolean {
        return if (!isRunning) {
            log.info("[WARN] Message scheduler already stopped.")
            false
        } else {
            isRunning = false
            log.info("[STOP] Message scheduler stopped.")
            true
        }
    }

    override fun scheduleMessage(chatId: Long, callbackType: CallbackType) {
        if (!callbackType.notified) {
            permanentMessage(chatId, callbackType)
        } else {
            log.info("New message postponed: ${callbackType.javaClass.simpleName}")
            timerQueue.schedule(ScheduledMessageTask(chatId, callbackType), Params.testTimer)
        }
    }

    private fun permanentMessage(chatId: Long, callbackType: CallbackType) {
        log.info("New message for sending: ${callbackType.javaClass.simpleName}")
        controller.send(chatId, callbackType)
    }

    inner class ScheduledMessageTask(private val chatId: Long, private val callbackType: CallbackType) : TimerTask() {
        override fun run() {
            permanentMessage(chatId, callbackType)
        }
    }

    companion object {
        const val SLEEP_TIME: Long = 1000
    }
}
