package callback

import command.Command

sealed class Callback {

    companion object {
        fun getCallback(callback: String, callbackData: String): Callback {
            return when (callback) {
                SetLanguage.CALLBACK -> SetLanguage(callbackData.toLong())
                SetCategory.CALLBACK -> SetCategory(callbackData.toLong())
                Answer.CALLBACK -> Answer(callbackData.toBoolean())
                Exam.CALLBACK -> Exam(callbackData.toBoolean())
                else -> None
            }
        }
    }

    data class SetLanguage(val languageId: Long) : Command() {
        companion object {
            const val CALLBACK = "langtestsetlanguage"
            fun buildCallBackQuery(languageId: Long): String = "$CALLBACK $languageId"
        }
    }
    data class SetCategory(val categoryId: Long) : Command() {
        companion object {
            const val CALLBACK = "langtestsetcategory"
            fun buildCallBackQuery(categoryId: Long): String = "$CALLBACK $categoryId"
        }
    }
    data class Answer(val isCorrect: Boolean) : Command() {
        companion object {
            const val CALLBACK = "langtestanswer"
            fun buildCallBackQuery(isCorrect: Boolean): String = "$CALLBACK $isCorrect"
        }
    }
    data class Exam(val start: Boolean) : Command() {
        companion object {
            const val CALLBACK = "langtestexam"
            fun buildCallBackQuery(start: Boolean): String = "$CALLBACK $start"
        }
    }
    object None : Callback()
}
