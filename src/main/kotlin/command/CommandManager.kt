package command

import ability.IAbility
import ability.IAbilityManager
import ability.langTestAbility.AbilityCommand
import ability.langTestAbility.LangTestAbility
import ability.langTestAbility.LangTestCommand
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
import state.DataState

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
        when (val command = commandRequestData.command) {
            Command.HelpCommand -> sendMessage(
                chatId,
                MessageBuilder.setChatId(chatId)
                    .enableMarkdown(true)
                    .setText(SystemMessages.helpMsg)
                    .build()
            )
            Command.NotForMe -> messageSender.send(Empty)
            Command.TimeToNextTestCommand -> CoroutineScope(Dispatchers.Default).launch {
                sendTimeToNextTest(chatId)
            }
            Command.None -> messageSender.send(Empty)

            Command.StartCommand -> langTestAbility?.subscribe(chatId)
            Command.StopCommand -> langTestAbility?.unsubscribe(chatId)

            is Command.StartQuizCallback -> langTestAbility?.commandAction(LangTestCommand(chatId, messageId, command))
            is Command.CorrectAnswerCallback -> langTestAbility?.commandAction(LangTestCommand(chatId, messageId, command))
            is Command.AskExamCallback -> langTestAbility?.commandAction(LangTestCommand(chatId, messageId, command))
            is Command.SetCategoryCallback -> langTestAbility?.commandAction(LangTestCommand(chatId, messageId, command))
            is Command.SetLanguageCallBack -> langTestAbility?.commandAction(LangTestCommand(chatId, messageId, command))
            Command.SetLanguageCommand -> TODO()
            is Command.IncorrectAnswerCallback -> langTestAbility?.commandAction(LangTestCommand(chatId, messageId, command))
            Command.ContinueQuiz -> langTestAbility?.commandAction(LangTestCommand(chatId, messageId, command))
        }
    }

    private fun <T : IAbility<out AbilityCommand>> getAbility(abilityClass: Class<out T>): T? {
        return try {
            abilityManager.getAbility(abilityClass)
        } catch (e: NullPointerException) {
            log.error("[ERROR] Ability not found", e)
            null
        }
    }

    private fun sendMessage(chatId: Long, sendMessage: SendMessage) {
        messageSender.send(UserMessage(chatId, sendMessage))
    }

    private suspend fun sendTimeToNextTest(chatId: Long) {
        try {
            when (val user = userRepo.getUserByChatId(chatId)) {
                DataState.Empty -> TODO()
                is DataState.Failure -> TODO()
                is DataState.Success -> {
                    val text = SystemMessages.nextTestNotifyMessage(user.data.breakTimeInMillis)
                    val msg = MessageBuilder.setChatId(chatId)
                        .setText(text)
                        .build()
                    messageSender.send(UserMessage(chatId, msg))
                }
            }
        } catch (e: NullPointerException) {
            val msg = MessageBuilder.setChatId(chatId)
                .setText(SystemMessages.notRegistered)
                .build()
            messageSender.send(UserMessage(chatId, msg))
        }
    }
}
