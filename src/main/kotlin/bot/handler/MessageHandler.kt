package bot.handler

import controller.LangTestController
import bot.MessageSender
import command.Command
import controller.DefaultController
import data.IRequestData
import data.IResponseData
import org.slf4j.LoggerFactory
import res.Params

class MessageHandler(
    private val messageSender: MessageSender
) : IHandlerReceiver {

    private val langTestController = LangTestController(this)
    private val defaultController = DefaultController(this)
    private val botName = Params.botName
    
    private val log = LoggerFactory.getLogger(this::class.java)

    fun handleRequest(requestData: IRequestData) {
        log.debug("Command for handle ${requestData.command}. For chatId: ${requestData.chatId}")
        when (requestData.command) {
            Command.None -> {}
            Command.NotForMe -> {}
            Command.HelpCommand -> defaultController.commandAction(requestData)
            
            else -> langTestController.commandAction(requestData)
        }
    }

    override fun receiveData(responseData: IResponseData) {
        messageSender.send(responseData)
    }
}
