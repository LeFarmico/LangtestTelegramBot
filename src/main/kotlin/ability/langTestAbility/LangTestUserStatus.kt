package ability.langTestAbility

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class LangTestUserStatus {

    private val status = UserStatus.DEFAULT
    private val testQueue: Queue<TestQuestionData> = ConcurrentLinkedQueue()

    enum class UserStatus {
        DEFAULT, WAITING_FOR_ANSWER, PROCESSING
    }
}