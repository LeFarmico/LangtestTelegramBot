package handler

import callback.Callback
import callback.CallbackParser
import callback.CallbackWithInfo
import org.telegram.telegrambots.meta.api.objects.Update
import utils.getChatId

class CallbackHandler(update: Update) {

    private val callbackData = update.callbackQuery.data
    private val chatId = update.getChatId
    private val messageId = update.callbackQuery.message.messageId

    fun handle(callbackWithInfo: (CallbackWithInfo) -> Unit) {
        callbackWithInfo(
            CallbackWithInfo(getCallback(callbackData), chatId, messageId)
        )
    }

    private fun getCallback(callbackData: String): Callback {
        return CallbackParser(callbackData).toCallback()
    }
}
