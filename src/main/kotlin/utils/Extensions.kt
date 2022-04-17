package utils

import org.telegram.telegrambots.meta.api.objects.Update

val Update.getChatId: Long get() = 
    this.message?.chatId
        ?: this.callbackQuery.message.chatId

val Update.getMessageId: Int get() =
    this.message?.messageId ?: this.callbackQuery.message.messageId

val Update.getUserId: String get() {
    val userId = this.message?.from?.id ?: this.callbackQuery.message.from.id
    return userId.toString()
}

val Update.getFirstName: String get() =
    this.message?.from?.firstName ?: this.callbackQuery.from.firstName

val Update.getLastName: String get() =
    this.message?.from?.lastName ?: this.callbackQuery.from.lastName

val Update.getUserName: String get() =
    this.message?.from?.userName ?: this.callbackQuery.from.userName

val Update.getText: String get() {
    return if (this.callbackQuery == null) {
        this.message?.text ?: ""
    } else {
        this.callbackQuery.data
    }
}
