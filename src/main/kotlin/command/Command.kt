package command

sealed class Command {

    companion object {
        fun getCommand(text: String): Command {
            return when (text.trim().lowercase()) {
                "start" -> Start
                "help" -> Help
                "begintest" -> BeginTest
                "timetonexttest" -> TimeToNextTest
                else -> None
            }
        }
    }

    object None : Command()
    object NotForMe : Command()
    object Start : Command()
    object Help : Command()
    object BeginTest : Command()
    object TimeToNextTest : Command()
}
