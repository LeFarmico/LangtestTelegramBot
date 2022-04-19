package interactor

import entity.QuizData
import entity.QuizStats
import entity.QuizWord
import repository.QuizRepository
import repository.UserRepository
import state.DataState

class LangTestInteractor(
    private val userRepository: UserRepository,
    private val quizRepository: QuizRepository,
) : ILangTestInteractor {

    override suspend fun registerUser(
        clientId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        breakTimeInMillis: Long,
        wordsLimit: Int
    ): DataState<QuizData> {
        return userRepository.addClient(clientId, chatId, languageId, categoryId, breakTimeInMillis, wordsLimit)
    }

    override suspend fun deleteUser(chatId: Long): Boolean {
        return userRepository.deleteUserChatId(chatId)
    }

    override suspend fun getClientData(chatId: Long): DataState<QuizData> {
        return userRepository.getUserByChatId(chatId)
    }

    override suspend fun updateClientData(
        clientId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long,
        wordsInTest: Int
    ): DataState<QuizData> {
        return userRepository.updateUser(clientId, chatId, languageId, categoryId, nextQuizTime, wordsInTest)
    }

    override suspend fun getNextWordOrScheduleQuiz(chatId: Long): DataState<QuizWord> {
        return quizRepository.getNextQuizWord(chatId)
    }

    override suspend fun setAnswerForQuiz(chatId: Long, quizWordId: Long, answer: Boolean): DataState<QuizStats> {
        return quizRepository.setAnswerForQuizWord(chatId, quizWordId, answer)
    }

    override suspend fun resetQuiz(chatId: Long): Boolean {
        return quizRepository.resetQuiz(chatId)
    }

    override suspend fun createQuizForUser(chatId: Long): Boolean {
        return quizRepository.createQuizWords(chatId)
    }
}
