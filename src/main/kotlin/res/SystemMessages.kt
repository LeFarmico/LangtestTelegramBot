package res

object SystemMessages {
    
    const val helpMsg = 
        "*Это вспомогательное сообщение - здесь назодятся всё что я умею.* \n" +
            "[/start](/start) - приветственное сообщение \n" +
            "[/help](/help) - узнать все что я умею \n" +
            "[/langtest](/langtest) - запуск теста \n" +
            "[/timetorepeat](/timetorepeat) - установить время, через которое будет приходить новый тест \n" +
            "[/wordsintest](/wordsintest) - Установить количество слов в тесте \n" +
            "[/addword](/addword) - добавить слово \n"
    
    const val startMsg = "Привет я бот Испонского языка \n" +
        "Я создан чтобы помочь изучать его \n" + 
        "Чтобы узнать что я умею - введи команду [/help](/help) \n"

    const val rightAnswer = "Верный ответ"

    const val wrongAnswer = "Неверный ответ"

    const val chooseLanguage = "Выберите язык из предложенных"

    const val askForExam = "Вы ответили на все вопросы. Хотите начать экзамен?"

    const val yes = "Да"

    const val no = "Нет"

    const val notRegistered = "Вы не зарегистрированны в системе"

    fun nextTestNotifyMessage(timeInMillis: Long): String {
        val min = timeInMillis / 1000 / 60
        return when {
            min < 1 -> {
                "Следующий тест начнется менее чем через минуту"
            }
            min == 1L -> {
                "Следующий тест начнется через минуту"
            }
            min in 2..4 -> {
                "Следующий тест начнется через $min минуты"
            }
            else -> {
                "Следующий тест начнется через $min минут"
            }
        }
    }
}
