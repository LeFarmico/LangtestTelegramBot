package entity

data class Quiz(val id: Long, val chatId: Long, val status: QuizStatus)

enum class QuizStatus {
    IN_PROCESS, WAITING
}