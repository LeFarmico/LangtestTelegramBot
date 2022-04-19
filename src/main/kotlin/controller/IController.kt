package controller

import bot.handler.IHandlerReceiver
import data.IRequestData

interface IController {

    val responseReceiver: IHandlerReceiver

    fun commandAction(requestData: IRequestData)
}
