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
                SetBreakTimeCommand.COMMAND -> SetBreakTimeCommand

                TimeToNextTestCommand.COMMAND -> TimeToNextTestCommand
                StartQuizCallback.COMMAND -> StartQuizCallback(commandData.toBoolean())
                SetLanguageCallBack.COMMAND -> SetLanguageCallBack(commandData.toLong())
                SetCategoryCallback.COMMAND -> SetCategoryCallback(commandData.toLong())
                CorrectAnswerCallback.COMMAND -> CorrectAnswerCallback(commandData.toLong())
                IncorrectAnswerCallback.COMMAND -> IncorrectAnswerCallback(commandData.toLong())
                SetLanguageCommand.COMMAND -> SetLanguageCommand
                GetQuizTest.COMMAND -> GetQuizTest
                SetBreakTimeCallback.COMMAND -> SetBreakTimeCallback(commandData.toLong())

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

    object SetBreakTimeCommand : Command() {
        const val COMMAND = "setnewbreaktime"
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
        val quizWordId: Long,
    ) : Command() {

        companion object {
            const val COMMAND = "corans"
            fun buildCallBackQuery(quizWordId: Long): String = "$COMMAND $quizWordId"
        }
    }

    data class IncorrectAnswerCallback(
        val quizWordId: Long
    ) : Command() {
        companion object {
            const val COMMAND = "incorans"
            fun buildCallBackQuery(wordId: Long): String = "$COMMAND $wordId"
        }
    }

    data class SetBreakTimeCallback(
        val breakTime: Long
    ) : Command() {
        companion object {
            const val COMMAND = "setbreaktime"
            fun buildCallBackQuery(breakTime: Long): String = "$COMMAND $breakTime"
        }
    }
//    data class AskExamCallback(val start: Boolean) : Command() {
//        companion object {
//            const val COMMAND = "langtestexam"
//            fun buildCallBackQuery(start: Boolean): String = "$COMMAND $start"
//        }
//    }
}
