package callback

interface ICallbackManager {
    
    fun addListener(callbackListener: CallbackListener)
    
    fun deleteListener(callbackListener: CallbackListener)
    
    fun checkForListener(chatId: Long, callbackClass: Class<out Callback>): CallbackListener?

    fun callbackAction(callbackWithInfo: CallbackWithInfo)
}
