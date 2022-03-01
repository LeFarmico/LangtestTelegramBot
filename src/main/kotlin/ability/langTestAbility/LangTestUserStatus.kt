package ability.langTestAbility

import entity.User
import entity.WordData
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class LangTestUserStatus(private val user: User) {

    private val wordQueue: Queue<WordData> = ConcurrentLinkedQueue()

    private var status = UserStatus.DEFAULT
    private var currentWord: WordData? = null
    private var answerList: List<String> = listOf()

    fun setWords(wordsList: List<WordData>) {
        answerList = wordsList.map { it.translate }
        wordQueue.addAll(
            wordsList.shuffled()
        )
    }

    fun next(): TestData? {
        val word = wordQueue.poll()
        currentWord = word
        status = UserStatus.WAITING_FOR_ANSWER
        return createTest(word)
    }

    private fun createTest(wordData: WordData?): TestData? {
        return try {
            val word = wordData!!.word
            val answer = wordData.translate
            val falseAnswers = answerList
                .shuffled()
                .filterNot { it == answer }
                .take(2)
            TestData(word, answer, falseAnswers)
        } catch (e: NullPointerException) {
            null
        }
    }

    fun answer(isCorrect: Boolean) {
        if (status == UserStatus.WAITING_FOR_ANSWER) {
            status = UserStatus.PROCESSING
            if (!isCorrect) {
                wordQueue.add(currentWord)
            }
        }
    }

    enum class UserStatus {
        DEFAULT, WAITING_FOR_ANSWER, PROCESSING
    }
}
