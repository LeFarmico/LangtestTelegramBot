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
                // LangTestAbility

                TimeToNextTestCommand.COMMAND -> TimeToNextTestCommand
                StartQuizCallback.COMMAND -> StartQuizCallback(commandData.toBoolean())
                SetLanguageCallBack.COMMAND -> SetLanguageCallBack(commandData.toLong())
                SetCategoryCallback.COMMAND -> SetCategoryCallback(commandData.toLong())
                CorrectAnswerCallback.COMMAND -> CorrectAnswerCallback(commandData.toLong())
                IncorrectAnswerCallback.COMMAND -> IncorrectAnswerCallback(commandData.toLong())
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

    data class CorrectAnswerCallback(val wordId: Long) : Command() {

        companion object {
            const val COMMAND = "correctlangtestanswer"
            fun buildCallBackQuery(wordId: Long): String = "$COMMAND $wordId"
        }
    }

    data class IncorrectAnswerCallback(val wordId: Long) : Command() {

        companion object {
            const val COMMAND = "incorrectlangtestanswer"
            fun buildCallBackQuery(wordId: Long): String = "$COMMAND $wordId"
        }
    }
//    data class AskExamCallback(val start: Boolean) : Command() {
//        companion object {
//            const val COMMAND = "langtestexam"
//            fun buildCallBackQuery(start: Boolean): String = "$COMMAND $start"
//        }
//    }
}
