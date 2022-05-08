package controller

import intent.LangTestState

interface IStateHandler {
    fun handleState(state: LangTestState, chatId: Long, messageId: Int)
}
