package interactor

class LangTestInteractor : ILangTestInteractor {

    override fun registerUser(
        userId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long,
        wordsInTest: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteUser(clientId: Long) {
        TODO("Not yet implemented")
    }

    override fun getClientData(clientId: Long) {
        TODO("Not yet implemented")
    }

    override fun updateClientData(
        userId: String,
        chatId: Long,
        languageId: Long,
        categoryId: Long,
        nextQuizTime: Long,
        wordsInTest: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun getNextWordOrScheduleQuiz(userId: String) {
        TODO("Not yet implemented")
    }

    override fun setAnswerForQuiz(userId: String, quizWordId: Long, answer: Boolean) {
        TODO("Not yet implemented")
    }

    override fun resetQuiz(userId: String) {
        TODO("Not yet implemented")
    }

    override fun createQuizForUser(userId: String) {
        TODO("Not yet implemented")
    }
}
