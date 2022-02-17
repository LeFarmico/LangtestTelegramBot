package ability

import bot.IMessageController
import entity.CallbackType
import entity.User
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import repository.UserRepository
import repository.WordsRepository
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class LangTestAbility(
    private val messageController: IMessageController,
    val chatId: Long
) : AbstractAbility(chatId) {

    private val log = LoggerFactory.getLogger(javaClass.simpleName)
    private val timer by lazy { Timer(true) }
    private val testQueue: Queue<Pair<Long, CallbackType>> = ConcurrentLinkedQueue()
    private lateinit var userRepo: UserRepository
    private lateinit var wordsRepository: WordsRepository
    private var user: User? = null

    override fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            if (abilityState != AbilityState.STARTED) {
                userRepo.addUser(chatId)
                user = userRepo.getUserByChatId(chatId)!!
                try {
                    createExam(chatId, user!!.categoryId)
                    action()
                } catch (e: NullPointerException) {
                    log.error("User with chatId: $chatId is not exist")
                }
            }
            super.start()
        }
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

    override fun finish() {
        testQueue.clear()
        super.finish()
    }

    private suspend fun addNewUser(chatId: Long): User? {
        userRepo.addUser(chatId)
        return userRepo.getUserByChatId(chatId)
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

    private fun scheduleNextExam() {
        try {
            timer.schedule(ScheduleExam(user), user!!.breakTimeInMillis)
        } catch (e: NullPointerException) {
            log.error("Next exam not scheduled", e)
        }
    }

    private fun createExam(chatId: Long, categoryId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            wordsRepository.getUnansweredWordsCategoryById(chatId, categoryId)
        }
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

    inner class ScheduleExam(private val user: User?) : TimerTask() {
        override fun run() {
            if (user != null) {
                createExam(user.chatId, user.categoryId)
            }
        }
    }

    companion object {
        const val TEST_COUNT = 5
    }
}
