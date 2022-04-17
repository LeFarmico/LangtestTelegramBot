package ability

import data.IRequestData

interface IController {
    
    fun commandAction(requestData: IRequestData)
}
