package command

import ability.IAbilityManager
import ability.langTestAbility.LangTestAbility
import bot.IMessageSender
import entity.Empty
import entity.UserMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import messageBuilders.MessageBuilder
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import repository.UserRepository
import res.SystemMessages

class CommandManager(private val abilityManager: IAbilityManager, private val messageSender: IMessageSender) : ICommandManager {

    lateinit var userRepo: UserRepository // TODO заимплементить

    override fun commandAction(commandWithInfo: CommandWithInfo) {
        val chatId = commandWithInfo.chatId
        when (commandWithInfo.parsedCommand.command) {
            Command.BeginTest -> abilityManager.addAndStartAbility(chatId, LangTestAbility(chatId) )
            Command.Help -> sendMessage(
                MessageBuilder.setChatId(chatId)
                    .enableMarkdown(true)
                    .setText(SystemMessages.helpMsg)
                    .build()
                )
            Command.Start -> sendMessage(
                MessageBuilder.setChatId(chatId)
                    .enableMarkdown(true)
                    .setText(SystemMessages.startMsg)
                    .build()
            )
            Command.NotForMe -> messageSender.send(Empty)
            Command.TimeToNextTest -> CoroutineScope(Dispatchers.Default).launch {
                sendTimeToNextTest(chatId)
            }
            Command.None -> messageSender.send(Empty)
        }
    }

    private fun sendMessage(sendMessage: SendMessage) {
        messageSender.send(UserMessage(sendMessage))
    }

    private suspend fun sendTimeToNextTest(chatId: Long) {
        try {
            val user = userRepo.getUserByChatId(chatId)!!
            val text = SystemMessages.nextTestNotifyMessage(user.breakTimeInMillis)
            val msg = MessageBuilder.setChatId(chatId)
                .setText(text)
                .build()
            messageSender.send(UserMessage(msg))
        } catch (e: NullPointerException) {
            val msg = MessageBuilder.setChatId(chatId)
                .setText(SystemMessages.notRegistered)
                .build()
            messageSender.send(UserMessage(msg))
        }
    }

}
