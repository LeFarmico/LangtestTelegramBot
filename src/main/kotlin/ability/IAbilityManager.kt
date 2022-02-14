package ability

interface IAbilityManager {
    
    val abilityMap: MutableMap<Long, AbstractAbility>

    fun addAbility(chatId: Long, ability: AbstractAbility)

    fun addAndStartAbility(chatId: Long, ability: AbstractAbility)

    fun startAbility(chatId: Long)

    fun finishAbility(chatId: Long)

    fun abilityAction(chatId: Long, any: Any? = null)
}
