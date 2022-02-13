package ability

import bot.IMessageController
import entity.CallbackType
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class LangTestAbility(private val messageController: IMessageController) {

    private val log = LoggerFactory.getLogger(javaClass.simpleName)

    private val testQueue: Queue<Pair<Long, CallbackType>> = ConcurrentLinkedQueue()
    private var isStarted: Boolean = false

    fun start(chatId: Long) {
        log.info("[START] LangTestAbility started for chatId: $chatId")
        if (!isStarted) {
            isStarted = true
            createExam(chatId)
            next()
        }
    }

    fun next() {
        val messageType = testQueue.poll()
        if (messageType != null) {
            log.info("Sending a new test.")
            messageController.schedule(messageType.first, messageType.second)
        } else {
            isStarted = false
        }
    }

    fun isStarted(): Boolean = isStarted

    private fun createExam(chatId: Long) {
        for (i in 1..TEST_COUNT) {
            val callbackType = CallbackType.SendMessage(createTestMessage(chatId))
            testQueue.add(Pair(chatId, callbackType))
        }
    }

    private fun createTestMessage(chatId: Long): SendMessage {
        return SendMessage().apply {
            this.chatId = chatId.toString() 
            text = "Choose right answer"
            replyMarkup = createButtonsList()
        }
    }

    private fun createButtonsList(): InlineKeyboardMarkup {

        val rightAnswerButton = InlineKeyboardButton().apply {
            text = "Right"
            callbackData = "right"
        }
        val wrongAnswerButton1 = InlineKeyboardButton().apply {
            text = "Wrong"
            callbackData = "/wrongAnswer"
        }
        val wrongAnswerButton2 = InlineKeyboardButton().apply {
            text = "Wrong"
            callbackData = "/wrongAnswer"
        }

        val buttonList = mutableListOf(rightAnswerButton, wrongAnswerButton1, wrongAnswerButton2)
        buttonList.shuffle()

        return InlineKeyboardMarkup(listOf(buttonList))
    }

    companion object {
        const val TEST_COUNT = 5
    }
}
