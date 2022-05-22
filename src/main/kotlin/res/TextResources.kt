package res

import entity.QuizViewData

object TextResources {
    
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

    // ---------- registration ------------

    const val startRegistration = "Начинаем регистрацию"

    const val startRegistrationFail = "Не удалось начать регистрацию."

    const val userNotRegistered = "Пользователь не зарегистрирован"

    const val finishRegistrationFail = "Не удалось завершить регистрацию."

    const val registerWarning = "Прежде чем начать, пройдите регистрацию."

    // ---------- data ----------

    const val dataNotFound = "Данные не найдены"

    const val getDataFail = "Не удалось получить данные."

    const val getUserDataFail = "Не удалось получить данные пользователя."

    const val updateDataFail = "Не удалось обновить данные"

    const val selectBreakTime = "Выберите время перерыва между тестами"

    const val breakTimeQuizNotification = "Викторина будет повторятся раз в "

    const val setBreakTimeFail = "Не удалось изменить время."

    // ----------- lang ---------

    const val langNotFound = "Язык не найден"

    const val getLangListFail = "Не удалось получить языки"

    const val setLangFail = "Не удалось выбрать язык"

    const val chooseLanguage = "Выберите язык из предложенных"

    // ----------- user ----------

    const val userDataDeleted = "Пользовательские данные удалены"

    const val userNotExist = "Пользователь не существует"

    const val userNotFound = "Пользователь не найден."

    // ----------- category -------------

    const val categoryListNotFound = "Категории не найдены"

    const val categoryNotFound = "Категоря не найдена"

    const val setCategoryFail = "Не удалось выбрать категорию"

    const val getCategoryListFail = "Не удалось получить категории"

    const val chooseCategory = "Выберите категорию из предложенных"

    // ----------- common ---------------

    const val rightAnswer = "Верный ответ"

    const val wrongAnswer = "Неверный ответ, поробуйте еще раз."

    const val yes = "Да"

    const val no = "Нет"

    const val unexpectedError = "Упс! что-то пошло не так..."

    // ----------- quiz -----------

    const val quizText = "Выберите правильный перевод слова: "

    const val startQuizMsg = "Начинаем викторину!"

    const val startQuizFail = "Не удалось начать викторину."

    const val quizStartQuestion = "Хотите начать викторину?"

    const val quizContinueQuestion = "Хотите продолжить викторину?"

    const val startAgain = "Начать заново?"

    const val createQuizFail = "Не удалось создать викторину, Попробуйте еще раз."

    const val quizResetted = "Викторина сброшена"

    const val quizNotFound = "Викторина не найдена."

    const val quizNotification = "Чтобы начать викторину нажмите /start"

    // ----------- quiz words ------------

    const val allWordsAnswered = "Вы ответили на все слова!"

    const val wordsNotFound = "Слова не найдены!"

    const val wordNotFound = "Слово не найдено!"

    const val setAnswerFail = "Неудалось зарегистрировать ответ."

    fun languageChooseMessage(languageName: String): String = "Вы выбрали $languageName язык."

    fun categoryChooseMessage(categoryName: String): String = "Вы выбрали категорию: $categoryName."

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
