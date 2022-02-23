package command

import callback.Callback

sealed class Command {

    companion object {
        fun getCommand(commandText: String, commandData: String): Command {
            return when (commandText.trim().lowercase()) {
                Start.COMMAND -> Start
                Help.COMMAND -> Help

                // LangTestAbility
                BeginTest.COMMAND -> BeginTest
                TimeToNextTest.COMMAND -> TimeToNextTest
                SetLanguage.COMMAND -> Callback.SetLanguage(commandData.toLong())
                SetCategory.COMMAND -> Callback.SetCategory(commandData.toLong())
                Answer.COMMAND -> Callback.Answer(commandData.toBoolean())
                Exam.COMMAND -> Callback.Exam(commandData.toBoolean())

                else -> None
            }
        }
    }

    object None : Command()
    object NotForMe : Command()
    object Start : Command() {
        const val COMMAND = "start"
    }
    object Help : Command() {
        const val COMMAND = "help"
    }
    object BeginTest : Command() {
        const val COMMAND = "begintest"
    }
    object TimeToNextTest : Command() {
        const val COMMAND = "timetonexttest"
    }
    data class SetLanguage(val languageId: Long) : Command() {
        companion object {
            const val COMMAND = "langtestsetlanguage"
            fun buildCallBackQuery(languageId: Long): String = "$COMMAND $languageId"
        }
    }
    data class SetCategory(val categoryId: Long) : Command() {
        companion object {
            const val COMMAND = "langtestsetcategory"
            fun buildCallBackQuery(categoryId: Long): String = "$COMMAND $categoryId"
        }
    }
    data class Answer(val isCorrect: Boolean) : Command() {
        companion object {
            const val COMMAND = "langtestanswer"
            fun buildCallBackQuery(isCorrect: Boolean): String = "$COMMAND $isCorrect"
        }
    }
    data class Exam(val start: Boolean) : Command() {
        companion object {
            const val COMMAND = "langtestexam"
            fun buildCallBackQuery(start: Boolean): String = "$COMMAND $start"
        }
    }
}
