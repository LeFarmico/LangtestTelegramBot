package command

sealed class Command {

    companion object {
        fun getCommand(text: String): Command {
            return when (text.trim().lowercase()) {
                "start" -> Start
                "help" -> Help
                "id" -> Id
                "begintest" -> BeginTest
                "rightanswer" -> RightAnswer
                "wronganswer" -> WrongAnswer
                "wordsindictionary" -> WordsInDictionary
                "timetonexttest" -> TimeToNextTest
                "addword" -> AddWord
                else -> None
            }
        }
    }

    object None : Command()
    object NotForMe : Command()
    object Start : Command()
    object Help : Command()
    object Id : Command()
    object BeginTest : Command()
    object RightAnswer : Command()
    object WrongAnswer : Command()
    object WordsInDictionary : Command()
    object TimeToNextTest : Command()
    object AddWord : Command()
}
