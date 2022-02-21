package callback

import org.slf4j.LoggerFactory

class CallbackManager : ICallbackManager {

    private val lock = Any()
    private val log = LoggerFactory.getLogger(javaClass.simpleName)

    private val listenerList: MutableList<CallbackListener> = mutableListOf()

    override fun addListener(callbackListener: CallbackListener) {
        synchronized(lock) {
            listenerList.add(callbackListener)
        }
    }

    override fun deleteListener(callbackListener: CallbackListener) {
        synchronized(lock) {
            listenerList.remove(callbackListener)
        }
    }

    override fun checkForListener(chatId: Long, callbackClass: Class<out Callback>): CallbackListener? {
        synchronized(lock) {
            return listenerList.find { it.chatId == chatId && it.callbackClass == callbackClass }
        }
    }

    override fun callbackAction(callbackWithInfo: CallbackWithInfo) {
        try {
            val chatId = callbackWithInfo.chatId
            val callbackClass = callbackWithInfo.callback.javaClass
            val listener = checkForListener(chatId, callbackClass)
            listener!!.action(callbackWithInfo)
            deleteListener(listener)
            log.info("[ACTION] Listener action is running: ${callbackWithInfo.callback.javaClass}")
        } catch (e: NullPointerException) {
            log.warn("[WARN] Listener not found or not registered")
        }
    }
}
