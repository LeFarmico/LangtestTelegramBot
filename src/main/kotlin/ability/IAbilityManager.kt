package ability

interface IAbilityManager {

    fun addAbility(abilityClass: Class<out IAbility>, ability: IAbility)

    fun removeAbility(abilityClass: Class<out IAbility>)

    fun <T : IAbility> getAbility(abilityClass: Class<T>): T?
}
