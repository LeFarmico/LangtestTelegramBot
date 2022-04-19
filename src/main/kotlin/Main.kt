import bot.Bot
import inject.DependencyInjection
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory
import res.Params

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger("main function")
    val bot = Bot(Params.botName, Params.token)
    startKoin { modules(listOf(DependencyInjection.module)) }
    runBlocking {
        try {
            bot.connect()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}
