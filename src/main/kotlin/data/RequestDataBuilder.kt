package data

import command.Command

class RequestDataBuilder {

    private var _chatId: Long = -1
    private var _messageId: Int = -1
    private var _userId: String = ""
    private var _userName: String? = null
    private var _firstName: String = ""
    private var _lastName: String = ""
    private var _command: Command? = null

    fun chatId(chatId: Long): RequestDataBuilder {
        _chatId = chatId
        return this
    }

    fun messageId(messageId: Int): RequestDataBuilder {
        _messageId = messageId
        return this
    }

    fun userId(userId: String): RequestDataBuilder {
        _userId = userId
        return this
    }

    fun userName(userName: String?): RequestDataBuilder {
        _userName = userName
        return this
    }

    fun firstName(firstName: String): RequestDataBuilder {
        _firstName = firstName
        return this
    }

    fun lastName(lastName: String): RequestDataBuilder {
        _lastName = lastName
        return this
    }

    fun command(command: Command): RequestDataBuilder {
        _command = command
        return this
    }

    fun build(): RequestData {
        return RequestData(_chatId, _messageId, _userId, _userName, _firstName, _lastName, _command!!)
    }
}
