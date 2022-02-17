package repository

interface UserStats {
    
    suspend fun addCorrectAnswer(wordId: Long, chatId: Long): Boolean
}
