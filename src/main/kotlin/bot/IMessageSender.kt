package bot

import entity.SendData

interface IMessageSender {

    fun send(sendData: SendData)
}
