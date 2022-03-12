package ability

import ability.langTestAbility.AbilityCommand

interface IAbilityManager {

    fun addAbility(abilityClass: Class<out IAbility<out AbilityCommand>>, ability: IAbility<out AbilityCommand>)

    fun removeAbility(abilityClass: Class<out IAbility<out AbilityCommand>>)

    fun <T : IAbility<out AbilityCommand>> getAbility(abilityClass: Class<out T>): T?
}
