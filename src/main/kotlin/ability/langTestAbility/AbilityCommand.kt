package ability.langTestAbility

import command.Command

interface AbilityCommand {
    abstract val chatId: Long
    abstract val messageId: Int
    abstract val command: Command
}

data class LangTestCommand(
    override val chatId: Long,
    override val messageId: Int,
    override val command: Command
) : AbilityCommand
