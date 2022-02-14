package bot

import ability.IAbilityManager

interface IBot {
    
    val controller: IMessageController
    val abilityManager: IAbilityManager
    
    suspend fun connect()
}
