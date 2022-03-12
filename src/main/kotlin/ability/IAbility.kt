package ability

import ability.langTestAbility.AbilityCommand

interface IAbility<T : AbilityCommand> {

    fun subscribe(chatId: Long)

    fun commandAction(data: T)
    
    fun unsubscribe(chatId: Long)
}
