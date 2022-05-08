package res

import entity.QuizViewData

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

    fun breakBetweenQuiz(timeInMillis: Long): String {
        val min = timeInMillis / 1000 / 60
        return when {
            min < 1 -> {
                "Менее минуты"
            }
            min == 1L -> {
                "$min минута"
            }
            min in 2..4 -> {
                "$min минуты"
            }
            else -> {
                "$min минут"
            }
        }
    }

    fun userDataMessage(quizViewData: QuizViewData): String {
        val msg = StringBuilder()
        msg.append("Язык: ${quizViewData.languageName} \n")
        msg.append("Категория: ${quizViewData.categoryName} \n")
        msg.append("Слов в тесте: ${quizViewData.wordsInQuiz} \n")
        msg.append("Номер текущего слова: ${quizViewData.currentWordNumber} \n")
        msg.append("Перерыв между викторинами: ${quizViewData.breakTime} \n")
        return msg.toString()
    }

    fun userSettingsMessage(language: String, category: String, isUpdate: Boolean = false): String {
        return if (isUpdate) {
            val msg = StringBuilder()
            msg.append("Ваши данные обновлены! \n")
            msg.append("Язык: $language \n")
            msg.append("Категория: $category")
            msg.toString()
        } else {
            val msg = StringBuilder()
            msg.append("Язык: $language \n")
            msg.append("Категория: $category")
            msg.toString()
        }
    }
}
