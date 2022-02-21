package callback

data class CallbackWithInfo(val callback: Callback, val chatId: Long, val messageId: Int)
