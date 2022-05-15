package messageBuilders

import command.Command
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class TestMessageBuilder(
    chatId: Long,
    private val wordId: Long
) {
    
    private val message = MessageBuilder.setChatId(chatId)

    private var buttonBuilder = ButtonBuilder.setUp()

    fun setQuizText(text: String, wordToTranslate: String): TestMessageBuilder {
        message.setText("$text $wordToTranslate")
        return this
    }

    fun addIncorrectButton(text: String): TestMessageBuilder {
        buttonBuilder.addButton(text, Command.CorrectAnswerCallback.buildCallBackQuery(wordId))
        return this
    }

    fun addIncorrectButtonList(textList: List<String>): TestMessageBuilder {
        for (i in textList.indices) {
            buttonBuilder = buttonBuilder.addButton(
                textList[i],
                Command.IncorrectAnswerCallback.buildCallBackQuery(wordId)
            )
        }
        return this
    }

    fun addCorrectButton(text: String): TestMessageBuilder {
        buttonBuilder = buttonBuilder.addButton(
            text,
            Command.CorrectAnswerCallback.buildCallBackQuery(wordId)
        )
        return this
    }

    fun build(): SendMessage {
        val buttons = buttonBuilder.build(shuffled = true, isVertical = true)
        return message.setButtons(buttons)
            .build()
    }
    
    companion object {
        fun setChatId(chatId: Long, wordId: Long): TestMessageBuilder {
            return TestMessageBuilder(chatId, wordId)
        }
    }
}
