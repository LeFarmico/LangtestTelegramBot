package command

import ability.IAbility
import ability.IAbilityManager
import ability.langTestAbility.AbilityCommand
import ability.langTestAbility.LangTestAbility
import bot.IMessageSender
import entity.Empty
import entity.UserMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import messageBuilders.MessageBuilder
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import repository.UserRepository
import res.SystemMessages

class CommandManager(
    private val abilityManager: IAbilityManager,
    private val messageSender: IMessageSender
) : ICommandManager {

    private val log = LoggerFactory.getLogger(javaClass.simpleName)
    lateinit var userRepo: UserRepository // TODO заимплементить
    private val langTestAbility by lazy { getAbility(LangTestAbility::class.java) }

    override fun commandAction(commandRequestData: CommandRequestData) {
        val chatId = commandRequestData.chatId
        val messageId = commandRequestData.messageId
        when (val command = commandRequestData.delimitedCommand.command) {
            Command.BeginTest -> langTestAbility?.subscribe(chatId)
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
            is Command.Answer -> langTestAbility?.action(AbilityCommand(chatId, messageId, command))
            is Command.Exam -> langTestAbility?.action(AbilityCommand(chatId, messageId, command))
            is Command.SetCategory -> langTestAbility?.action(AbilityCommand(chatId, messageId, command))
            is Command.SetLanguage -> langTestAbility?.action(AbilityCommand(chatId, messageId, command))
        }
    }

    private fun <T : IAbility> getAbility(abilityClass: Class<out T>): T? {
        return try {
            abilityManager.getAbility(abilityClass)
        } catch (e: NullPointerException) {
            log.error("Ability not found", e)
            null
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
