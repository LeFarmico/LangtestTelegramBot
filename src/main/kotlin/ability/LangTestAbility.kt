package ability

import bot.IMessageController
import entity.CallbackType
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class LangTestAbility(
    private val messageController: IMessageController,
    val chatId: Long
) : AbstractAbility(chatId) {

    private val log = LoggerFactory.getLogger(javaClass.simpleName)
    private val testQueue: Queue<Pair<Long, CallbackType>> = ConcurrentLinkedQueue()

    override fun start() {
        if (abilityState != AbilityState.STARTED) {
            createExam(chatId)
            action()
        }
        super.start()
    }

    override fun action(actionData: Any?) {
        try {
            if (actionData!! as Boolean) {
                testQueue.remove()
            } else {
                val test = testQueue.poll()
                testQueue.add(test)
            }
        } catch (e: Exception) {
            when (e) {
                is NullPointerException -> {
                    log.info("[INFO] ActionData is null for chatId: $chatId", e)
                }
                is TypeCastException -> {
                    log.error("[ERROR] Unexpected type for actionData: ${actionData!!.javaClass.simpleName}", e)
                }
                else -> {
                    log.error("[ERROR] Unexpected error: ", e)
                }
            }
        }
        sendTest()
    }

    private fun sendTest() {
        val messageType = testQueue.peek()
        if (messageType != null) {
            log.info("Sending a new test.")
            messageController.schedule(messageType.first, messageType.second)
        } else {
            abilityState = AbilityState.FINISHED
        }
    }

    override fun finish() {
        testQueue.clear()
        super.finish()
    }

    private fun createExam(chatId: Long) {
        for (i in 1..TEST_COUNT) {
            val callbackType = CallbackType.SendMessage(createTestMessage(chatId, i))
            testQueue.add(Pair(chatId, callbackType))
        }
    }

    private fun createTestMessage(chatId: Long, number: Int): SendMessage {
        return SendMessage().apply {
            this.chatId = chatId.toString() 
            text = "Choose right answer $number"
            replyMarkup = createButtonsList()
        }
    }

    private fun createButtonsList(): InlineKeyboardMarkup {

        val rightAnswerButton = InlineKeyboardButton().apply {
            text = "Right"
            callbackData = "langtestright"
        }
        val wrongAnswerButton1 = InlineKeyboardButton().apply {
            text = "Wrong"
            callbackData = "langtestwrong"
        }
        val wrongAnswerButton2 = InlineKeyboardButton().apply {
            text = "Wrong"
            callbackData = "langtestwrong"
        }

        val buttonList = mutableListOf(rightAnswerButton, wrongAnswerButton1, wrongAnswerButton2)
        buttonList.shuffle()

        return InlineKeyboardMarkup(listOf(buttonList))
    }

    companion object {
        const val TEST_COUNT = 5
    }
}
