package res

object SystemMessages {
    
    const val helpMsg = 
        "*Это вспомогательное сообщение - здесь назодятся всё что я умею.* \n" +
            "/start - приветственное сообщение \n" +
            "/help - узнать все что я умею \n" +
            "/langtest - запуск теста \n" +
            "/timetorepeat - установить время, через которое будет приходить новый тест \n" +
            "/wordsintest - Установить количество слов в тесте \n" +
            "/addword - добавить слово \n"
    
    const val startMsg = "Привет я бот Испонского языка \n" +
        "Я создан чтобы помочь изучать его \n" + 
        "Чтобы узнать что я умею - введи команду /help \n"

    const val rightAnswer = "Верный ответ"

    const val wrongAnswer = "Неверный ответ"

    const val chooseLanguage = "Выберите язык из предложенных"

    const val notFoundLanguage = "Язык не найден"

    const val chooseCategory = "Выберите категорию из предложенных"

    const val askForExam = "Вы ответили на все вопросы. Хотите начать экзамен?"

    const val yes = "Да"

    const val no = "Нет"

    const val notRegistered = "Вы не зарегистрированны в системе"

    const val quizText = "Выберите правильный перевод слова:"

    const val startQuizHelpMsg = "Чтобы начать викторину введите /start."

    const val startQuizMsg = "Начинаем викторину!"

    const val userNotFound = "Пользователь не найден."

    const val unexpectedError = "Упс! что-то пошло не так..."

    const val quizStartQuestion = "Хотите начать викторину?"

    const val quizContinueQuestion = "Хотите продолжить викторину?"

    const val startAgain = "Начать заново?"

    fun languageChooseMessage(languageName: String): String = "Вы выбрали $languageName язык."

    fun categoryChooseMessage(categoryName: String): String = "Вы выбрали $categoryName категорию."

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

    fun userSettingsMessage(language: String, category: String): String {
        val msg = StringBuilder()
        msg.append("Язык: $language \n")
        msg.append("Категория: $category")
        return msg.toString()
    }
}
