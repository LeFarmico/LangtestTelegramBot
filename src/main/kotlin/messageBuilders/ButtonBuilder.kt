package messageBuilders

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

class ButtonBuilder private constructor(private val buttonList: List<InlineKeyboardButton>) {

    fun addButton(text: String, callback: String): ButtonBuilder {
        val button = InlineKeyboardButton().apply {
            this.text = text
            this.callbackData = callback
        }
        val newButtonList = mutableListOf<InlineKeyboardButton>(button)
        newButtonList.addAll(buttonList)
        return ButtonBuilder(newButtonList)
    }

    fun build(isVertical: Boolean = false, shuffled: Boolean = false): InlineKeyboardMarkup {
        val buttons = if (shuffled) { buttonList.shuffled() } else { buttonList }
        return when (isVertical) {
            true -> {
                val verticalMarkup = buttons.map { listOf(it) }
                InlineKeyboardMarkup(verticalMarkup)
            }
            false -> {
                InlineKeyboardMarkup(listOf(buttons))
            }
        }
    }

    companion object {
        fun createFirstButton(text: String, callback: String): ButtonBuilder {
            val button = InlineKeyboardButton().apply {
                this.text = text
                this.callbackData = callback
            }
            val newButtonList = mutableListOf<InlineKeyboardButton>(button)
            return ButtonBuilder(newButtonList)
        }
    }
}
