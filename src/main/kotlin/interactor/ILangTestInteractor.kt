package interactor

interface ILangTestInteractor {

    fun registerUser(
        userId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long,
        wordsInTest: Int
    )

    fun deleteUser(clientId: Long)

    fun getClientData(clientId: Long)

    fun updateClientData(
        userId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long,
        wordsInTest: Int
    )

    fun getNextWordOrScheduleQuiz(userId: String)

    fun setAnswerForQuiz(userId: String, quizWordId: Long, answer: Boolean)

    fun resetQuiz(userId: String)

    fun createQuizForUser(userId: String)
}
