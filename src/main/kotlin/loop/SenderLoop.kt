package loop

import bot.Bot
import entity.MessageType
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.PriorityBlockingQueue

class SenderLoop(private val bot: Bot) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val lock = Object()
    private val availableActions: PriorityBlockingQueue<MessageType> = PriorityBlockingQueue()
    private val isAlive = true

    fun startLoop() {
        while (isAlive) {
            var message = availableActions.poll()
            if (message != null) {
                log.info("Trying to send: ${message.javaClass.simpleName}")
                send(message)
                // TODO message = availableActions.poll()
            } else {
                try {
                } catch (e: InterruptedException) {
                    log.error("Catch interrupt. Exit.", e)
                }
            }
        }
    }

    private fun send(messageType: MessageType) {
        when (messageType) {
            is MessageType.SendMessage -> {
                log.info("Use execute for: ${messageType.message.javaClass.simpleName}")
                bot.execute(messageType.message)
            }
            is MessageType.Sticker -> {
                log.info("Use sendSticker for: ${messageType.sticker.javaClass.simpleName}")
                bot.execute(messageType.sticker)
            }
            MessageType.Error -> {
                log.error("Execute error. Illegal type of message: ${messageType.javaClass.simpleName}", IllegalArgumentException())
            }
            MessageType.Empty -> {}
        }
    }
}

class Message(
    val callback: Runnable,
    val time: LocalDateTime,
    var next: Message?
)

class MessageQueue {
    var messages: Message? = null
    private val lock = Any()

    fun poll(): Message? {
        synchronized(lock) {
            val now = LocalDateTime.now()
            val current = messages
            return if (
                current == null ||
                now.toEpochSecond(ZoneOffset.UTC) < current.time.toEpochSecond(ZoneOffset.UTC)
            ) {
                null
            } else {
                messages = current.next
                current
            }
        }
    }

    fun add(newMessage: Message?): Boolean {
        if (newMessage == null) {
            return false
        }
        synchronized(lock) {
            var current: Message? = messages
            if (current == null) {
                messages = newMessage
            } else {
                var previous: Message?
                while (true) {
                    previous = current
                    current = current!!.next
                    if (current == null || newMessage.time < current.time) {
                        break
                    }
                }
                newMessage.next = previous!!.next
                previous.next = newMessage
            }
            return true
        }
    }
}
