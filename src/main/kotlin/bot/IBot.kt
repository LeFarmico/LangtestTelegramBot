package bot

import ability.IAbilityManager

interface IBot {

    val abilityManager: IAbilityManager

    suspend fun connect()
}
