package ability

import ability.langTestAbility.AbilityCommand

interface IAbility {

    fun subscribe(chatId: Long)

    fun action(data: AbilityCommand? = null)
    
    fun unsubscribe(chatId: Long)
}
