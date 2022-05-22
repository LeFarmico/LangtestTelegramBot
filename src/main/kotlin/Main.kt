import bot.Bot
import di.AppDi
import di.DataDI
import entity.UserData
import http.LangTestApi
import io.github.cdimascio.dotenv.Dotenv
import kotlinx.coroutines.*
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import org.slf4j.LoggerFactory
import res.Params
import res.PrivateData
import res.Resources

fun main() {
    val log = LoggerFactory.getLogger("main function")
    val bot = Bot(Params.botName, Params.token)
    Dotenv.configure().load().apply {
        Resources.HOST = get("SPRING_DOCKER_HOST")
        Resources.PORT = get("SPRING_LOCAL_PORT")
    }
    startKoin { modules(listOf(AppDi.module, DataDI.dataModule)) }
    val retrofitService: LangTestApi by inject(LangTestApi::class.java)
    
    runBlocking {
        try {
            login(retrofitService)
        } catch (e: RuntimeException) {
            register(retrofitService)
            login(retrofitService)
        }
        try {
            bot.connect()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}

suspend fun register(retrofitService: LangTestApi) {
    withContext(Dispatchers.IO) {
        val callback = retrofitService.register(
            UserData(PrivateData.login, PrivateData.password)
        )
        callback.execute()
    }
}

suspend fun login(retrofitService: LangTestApi) {
    withContext(Dispatchers.IO) {
        val response = retrofitService.login(UserData(PrivateData.login, PrivateData.password)).execute()
        if (response.body() != null) {
            val token = response.body()
            Resources.TOKEN = "Bearer " + token!!.jwtToken
        } else {
            throw RuntimeException("Login failure")
        }
    }
}
