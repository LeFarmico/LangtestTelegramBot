import bot.Bot
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("main function")
    val bot = Bot("My_personal_help_bot", "1120439674:AAGGxeQ1uP4T2bMvdA3ESu1BcTEdeE3g-yU")

    runBlocking {
        try {
            bot.connect()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
