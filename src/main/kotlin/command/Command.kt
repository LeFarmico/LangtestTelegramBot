package command

sealed class Command {

    companion object {
        fun getCommand(commandText: String, commandData: String): Command {
            return when (commandText.trim().lowercase()) {
                StartCommand.COMMAND -> StartCommand
                StopCommand.COMMAND -> StopCommand
                HelpCommand.COMMAND -> HelpCommand
                GetUserData.COMMAND -> GetUserData
                ResetQuiz.COMMAND -> ResetQuiz
                RestartQuiz.COMMAND -> RestartQuiz

                TimeToNextTestCommand.COMMAND -> TimeToNextTestCommand
                StartQuizCallback.COMMAND -> StartQuizCallback(commandData.toBoolean())
                SetLanguageCallBack.COMMAND -> SetLanguageCallBack(commandData.toLong())
                SetCategoryCallback.COMMAND -> SetCategoryCallback(commandData.toLong())
                CorrectAnswerCallback.COMMAND -> CorrectAnswerCallback.parseCallback(commandData)
                IncorrectAnswerCallback.COMMAND -> IncorrectAnswerCallback.parseCallback(commandData)
                SetLanguageCommand.COMMAND -> SetLanguageCommand
                GetQuizTest.COMMAND -> GetQuizTest

                else -> None
            }
        }
    }

    object None : Command()
    object NotForMe : Command()

    object SetLanguageCommand : Command() {
        const val COMMAND = "setQuizLanguage"
    }
    object StartCommand : Command() {
        const val COMMAND = "start"
    }
    object StopCommand : Command() {
        const val COMMAND = "stop"
    }

    object HelpCommand : Command() {
        const val COMMAND = "help"
    }

    object TimeToNextTestCommand : Command() {
        const val COMMAND = "timetonexttest"
    }

    object GetUserData : Command() {
        const val COMMAND = "stats"
    }

    object ResetQuiz : Command() {
        const val COMMAND = "resetquiz"
    }

    object GetQuizTest : Command() {
        const val COMMAND = "continuequiz"
        fun buildCallBackQuery(): String = COMMAND
    }

    object RestartQuiz : Command() {
        const val COMMAND = "restartquiz"
    }

    data class StartQuizCallback(val start: Boolean) : Command() {

        companion object {
            const val COMMAND = "startquiz"
            fun buildCallBackQuery(start: Boolean): String = "$COMMAND $start"
        }
    }

    data class SetLanguageCallBack(val languageId: Long) : Command() {

        companion object {
            const val COMMAND = "langtestsetlanguage"
            fun buildCallBackQuery(languageId: Long): String = "$COMMAND $languageId"
        }
    }

    data class SetCategoryCallback(val categoryId: Long) : Command() {

        companion object {
            const val COMMAND = "langtestsetcategory"
            fun buildCallBackQuery(categoryId: Long): String = "$COMMAND $categoryId"
        }
    }

    data class CorrectAnswerCallback(
        val wordId: Long,
        val wordOriginal: String,
        val translation: String
    ) : Command() {

        companion object {
            const val COMMAND = "correctlangtestanswer"
            fun buildCallBackQuery(
                wordId: Long,
                wordOriginal: String,
                translation: String
            ): String = "$COMMAND ${wordId}_${wordOriginal}_$translation"

            fun parseCallback(callback: String): CorrectAnswerCallback {
                val parameterList = callback.split("_")
                return CorrectAnswerCallback(
                    parameterList[0].toLong(),
                    parameterList[1],
                    parameterList[2]
                )
            }
        }
    }

    data class IncorrectAnswerCallback(
        val wordId: Long,
        val wordOriginal: String,
        val wrongTranslation: String
    ) : Command() {
        companion object {
            const val COMMAND = "incorrectlangtestanswer"
            fun buildCallBackQuery(
                wordId: Long,
                wordOriginal: String,
                wrongTranslation: String
            ): String = "$COMMAND ${wordId}_${wordOriginal}_$wrongTranslation"

            fun parseCallback(callback: String): IncorrectAnswerCallback {
                val parameterList = callback.split("_")
                return IncorrectAnswerCallback(
                    parameterList[0].toLong(),
                    parameterList[1],
                    parameterList[2]
                )
            }
        }
    }
//    data class AskExamCallback(val start: Boolean) : Command() {
//        companion object {
//            const val COMMAND = "langtestexam"
//            fun buildCallBackQuery(start: Boolean): String = "$COMMAND $start"
//        }
//    }
}
