package bot

import callback.ICallbackManager
import command.ICommandManager
import handler.CallbackHandler
import handler.MessageHandler
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class MessageReceiver(private val commandManager: ICommandManager, private val callbackManager: ICallbackManager) : IMessageReceiver {

    private val receiveQueue: Queue<Update> = ConcurrentLinkedQueue()
    private val log = LoggerFactory.getLogger(this::class.java)
    var isRunning = true

    override suspend fun start() {
        log.info("[START] Message receiver. Receiver.class: ${javaClass.simpleName}")
        isRunning = true
        while (isRunning) {
            var update = receiveQueue.poll()
            while (update != null) {
                log.info("New object for analyze in queue: ${update.javaClass.simpleName}")
                receive(update)
                update = receiveQueue.poll()
            }
            try {
                delay(Bot.SLEEP_TIME)
            } catch (e: InterruptedException) {
                log.error("Catch interrupt. Exit.", e)
            }
        }
    }

    override fun add(update: Update) {
        receiveQueue.add(update)
    }

    override fun isReceiverStarted(): Boolean = isRunning

    override fun stop(): Boolean {
        return if (!isRunning) {
            log.info("[WARN] Message receiver already stopped.")
            false
        } else {
            isRunning = false
            log.info("[STOP] Message receiver stopped.")
            true
        }
    }

    private fun receive(update: Update) {
        log.info("Handle receiver: $update")
        if (checkForCallback(update)) {
            CallbackHandler(update).handle {
                callbackManager.callbackAction(it)
            }
        } else {
            MessageHandler(update).handle {
                // TODO handle result
            }
        }
//        controller.schedule(chatId, messageType)
    }

    private fun checkForCallback(update: Update): Boolean {
        return update.callbackQuery != null
    }
}
