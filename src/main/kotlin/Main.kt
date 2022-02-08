import bot.Bot
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import service.MessageReceiver
import service.MessageSender

class Main {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val bot = Bot("My_personal_help_bot", "1120439674:AAGGxeQ1uP4T2bMvdA3ESu1BcTEdeE3g-yU")

    private val msgReceiver = MessageReceiver(bot)
    private val msgSender = MessageSender(bot)

    init {
        try {
            bot.connect()
            log.info("Bot connected")
        } catch (e: Exception) {
            log.error(e.message, e)
        }

        CoroutineScope(Dispatchers.Default).launch {
            msgReceiver.run()
        }
        CoroutineScope(Dispatchers.Default).launch {
            msgSender.run()
        }
    }

    companion object {
        const val PRIORITY_FOR_SENDER = 1
        const val PRIORITY_FOR_RECEIVER = 3
        const val BOT_ADMIN = "505567555"
    }
}

fun main(args: Array<String>) {
    Main()
}


