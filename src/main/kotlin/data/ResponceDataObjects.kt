package data

import messageBuilders.ButtonBuilder
import messageBuilders.DeleteMessageBuilder
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.io.Serializable

data class UserMessage(
    override val chatId: Long,
    override val message: BotApiMethod<Message>
) : IResponseData

data class EditUserMessage(
    override val chatId: Long,
    override val message: BotApiMethod<Serializable>
) : IResponseData

data class DeleteUserMessage(
    override val chatId: Long,
    override val message: BotApiMethod<Boolean>
) : IResponseData

object Empty : IResponseData {
    override val chatId: Long = 0
    override val message: BotApiMethod<Nothing>
        get() = throw IllegalArgumentException("That object must be handled as default value")
}

class ResponseFactory private constructor(val _chatId: Long) {

    fun message(message: String): MessageResponse {
        return MessageResponse(this, message)
    }

    fun buildSendMessageObject(sendMessage: SendMessage): UserMessage {
        return UserMessage(_chatId, sendMessage)
    }

    fun editCurrent(currentMessageId: Int): EditMessageResponse {
        return EditMessageResponse(this, currentMessageId)
    }

    fun deleteCurrent(currentMessageId: Int): DeleteMessageResponse {
        return DeleteMessageResponse(this, currentMessageId)
    }

    fun empty(): Empty {
        return Empty
    }

    companion object {
        fun builder(chatId: Long): ResponseFactory {
            return ResponseFactory(chatId)
        }
    }

    abstract class ResponseBuilder<T : IResponseData> {
        abstract fun build(): T
    }
    abstract class ResponseConstructor<T : IResponseData>(
        private val responseFactory: ResponseFactory
    ) : ResponseBuilder<T>() {

        protected val buttonList: MutableList<InlineKeyboardButton> = mutableListOf()
        protected var isVertical: Boolean = false
        protected var shuffled: Boolean = false
        protected var isEnabledMarkdown: Boolean = false

        fun addButton(text: String, callback: String): ResponseConstructor<T> {
            val button = InlineKeyboardButton().apply {
                this.text = text
                this.callbackData = callback
            }
            buttonList.add(button)
            return this
        }

        fun setButtons(buttonList: () -> List<InlineKeyboardButton>): ResponseConstructor<T> {
            this.buttonList.clear()
            this.buttonList.addAll(buttonList.invoke())
            return this
        }

        fun buttonParams(isVertical: Boolean = false, shuffled: Boolean = false): ResponseConstructor<T> {
            this.isVertical = isVertical
            this.shuffled = shuffled
            return this
        }

        fun markdown(isEnabled: Boolean): ResponseConstructor<T> {
            isEnabledMarkdown = isEnabled
            return this
        }
    }

    class MessageResponse(
        private val responseFactory: ResponseFactory,
        private val message: String
    ) : ResponseConstructor<UserMessage>(responseFactory) {
        override fun build(): UserMessage {
            val buttons = ButtonBuilder.setUp()
                .setButtons(buttonList)
                .build(isVertical, shuffled)

            val message = SendMessage().apply {
                chatId = responseFactory._chatId.toString()
                text = message
                replyMarkup = buttons
                enableMarkdown(isEnabledMarkdown)
            }
            return UserMessage(responseFactory._chatId, message)
        }
    }
    class EditMessageResponse(
        private val responseFactory: ResponseFactory,
        private val _messageId: Int
    ) : ResponseConstructor<EditUserMessage>(responseFactory) {

        private var message: String = ""

        fun message(message: String): EditMessageResponse {
            this.message = message
            return this
        }

        override fun build(): EditUserMessage {
            val buttons = ButtonBuilder.setUp()
                .setButtons(buttonList)
                .build(isVertical, shuffled)

            val editMessage = EditMessageText().apply {
                chatId = responseFactory._chatId.toString()
                text = message
                replyMarkup = buttons
                enableMarkdown(isEnabledMarkdown)
                messageId = _messageId
            }
            return EditUserMessage(responseFactory._chatId, editMessage)
        }
    }

    class DeleteMessageResponse(
        private val responseFactory: ResponseFactory,
        private val _messageId: Int
    ) : ResponseBuilder<DeleteUserMessage>() {
        override fun build(): DeleteUserMessage {
            val deleteMessage = DeleteMessageBuilder
                .chatAndMessageId(responseFactory._chatId, _messageId)
                .build()
            return DeleteUserMessage(responseFactory._chatId, deleteMessage)
        }
    }
}
