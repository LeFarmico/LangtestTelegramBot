import bot.Bot
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import res.Params

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("main function")
    val bot = Bot(Params.botName, Params.token)

    runBlocking {
        try {
            bot.connect()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
