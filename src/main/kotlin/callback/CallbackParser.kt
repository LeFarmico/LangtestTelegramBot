package callback

class CallbackParser(private val callbackText: String) {

    fun toCallback(): Callback {
        val text = callbackText.trim().lowercase()
        val delimitedCallback = getDelimitedCallback(text)

        return Callback.getCallback(delimitedCallback.callback, delimitedCallback.callbackData ?: "")
    }

    private fun getDelimitedCallback(callbackText: String): DelimitedCallback {
        var delimitedCallback = DelimitedCallback(callbackText)

        takeIf { callbackText.contains(" ") }
            ?.run {
                val spaceIndex = callbackText.indexOf(" ")
                delimitedCallback = DelimitedCallback(
                    callback = callbackText.substring(0, spaceIndex),
                    callbackData = callbackText.substring(spaceIndex + 1)
                )
            }
        return delimitedCallback
    }

    data class DelimitedCallback(val callback: String, val callbackData: String? = null)
}
