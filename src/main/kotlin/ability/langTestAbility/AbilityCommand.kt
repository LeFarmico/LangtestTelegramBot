package ability.langTestAbility

import command.Command

data class AbilityCommand(val chatId: Long, val messageId: Int, val command: Command)
