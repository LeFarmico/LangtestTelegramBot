package callback

class CallbackListener(
    val chatId: Long,
    val callbackClass: Class<Callback>,
    private val onAction: (CallbackWithInfo) -> Unit
) {

    fun action(callbackWithInfo: CallbackWithInfo) {
        onAction(callbackWithInfo)
    }
}
