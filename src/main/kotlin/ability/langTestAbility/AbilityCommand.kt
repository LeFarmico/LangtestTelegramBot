package ability.langTestAbility

import command.Command

interface AbilityCommand {
    val chatId: Long
    val messageId: Int
    val command: Command
}

data class LangTestAbilityCommand(
    override val chatId: Long,
    override val messageId: Int,
    override val command: Command
) : AbilityCommand
