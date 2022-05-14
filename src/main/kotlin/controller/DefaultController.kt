package controller

import bot.handler.IHandlerReceiver
import command.Command
import data.IRequestData
import data.ResponseFactory
import res.TextResources

class DefaultController(
    override val responseReceiver: IHandlerReceiver
) : IController {

    override fun commandAction(requestData: IRequestData) {
        when (requestData.command) {
            Command.HelpCommand -> {
                val response = ResponseFactory.builder(requestData.chatId)
                    .message(TextResources.helpMsg)
                    .build()
                responseReceiver.receiveData(response)
            }
            else -> throw RuntimeException("Wrong command for controller")
        }
    }
}
