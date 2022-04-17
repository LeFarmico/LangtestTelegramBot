package bot

import data.IResponseData

interface IMessageSender {

    fun send(responseData: IResponseData)
}
