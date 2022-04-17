package bot.handler

import data.IResponseData

interface IHandlerReceiver {
    
    fun receiveData(responseData: IResponseData)
}
