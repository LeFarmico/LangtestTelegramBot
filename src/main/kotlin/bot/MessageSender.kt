package bot

import entity.SendData
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import res.Params
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class MessageSender(private val controller: IMessageController) : IMessageSender {

    private val timedSendDataQueue: Queue<Pair<Long, SendData>> = ConcurrentLinkedQueue()
    private val timerQueue = Timer(true)
    private val log = LoggerFactory.getLogger(this::class.java)
    private var isRunning = true

    override fun send(sendData: SendData) {
        TODO("Not yet implemented")
    }

    suspend fun start() {
        log.info("[START] Message scheduler started. Sender.class: ${javaClass.simpleName}")
        isRunning = true
        while (isRunning) {
            var messageTypePair = timedSendDataQueue.poll()
            while (messageTypePair != null) {
                scheduleMessage(messageTypePair.first, messageTypePair.second)
                messageTypePair = timedSendDataQueue.poll()
            }
            try {
                delay(SLEEP_TIME)
            } catch (e: InterruptedException) {
                log.error("[ERROR] Thread was interrupted. Failure: ", e)
            }
        }
    }

    fun add(chatId: Long, callbackType: SendData) {
        timedSendDataQueue.add(Pair(chatId, callbackType))
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

    fun scheduleMessage(chatId: Long, callbackType: SendData) {
        if (!callbackType.notified) {
            permanentMessage(chatId, callbackType)
        } else {
            log.info("New message postponed: ${callbackType.javaClass.simpleName}")
            timerQueue.schedule(ScheduledMessageTask(chatId, callbackType), Params.testTimer)
        }
    }

    private fun permanentMessage(chatId: Long, callbackType: SendData) {
        log.info("New message for sending: ${callbackType.javaClass.simpleName}")
        controller.send(chatId, callbackType)
    }

    inner class ScheduledMessageTask(private val chatId: Long, private val callbackType: SendData) : TimerTask() {
        override fun run() {
            permanentMessage(chatId, callbackType)
        }
    }

    companion object {
        const val SLEEP_TIME: Long = 1000
    }


}
