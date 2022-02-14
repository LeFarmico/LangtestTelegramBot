package ability

import org.slf4j.LoggerFactory

class AbilityManager : IAbilityManager {
    
    private val log = LoggerFactory.getLogger(javaClass.simpleName)
    override val abilityMap: MutableMap<Long, AbstractAbility> = mutableMapOf()
    
    override fun addAbility(chatId: Long, ability: AbstractAbility) {
        if (abilityMap[chatId] == null) {
            log.info("[INFO] ${ability.javaClass.simpleName} added to chatId: $chatId")
            abilityMap[chatId] = ability
        } else {
            log.warn("[WARN] ability ${ability.javaClass.simpleName} already exist for chatId: $chatId")
        }
    }

    override fun addAndStartAbility(chatId: Long, ability: AbstractAbility) {
        if (abilityMap[chatId] == null) {
            log.info("[INFO] ${ability.javaClass.simpleName} added to chatId: $chatId")
            abilityMap[chatId] = ability
            abilityMap[chatId]!!.start()
        } else {
            log.warn("[WARN] ability ${ability.javaClass.simpleName} already exist for chatId: $chatId")
        }
    }

    override fun startAbility(chatId: Long) {
        try {
            val ability = abilityMap[chatId]!!
            ability.start()
            log.info("[INFO] ${ability.javaClass.simpleName} started for chatId: $chatId")
        } catch (e: NullPointerException) {
            log.error("[ERROR] Ability with chatId: $chatId is not found", e)
        }
    }
    
    override fun finishAbility(chatId: Long) {
        try {
            val ability = abilityMap[chatId]!!
            ability.finish()
            abilityMap.remove(chatId)
            log.info("[INFO] ${ability.javaClass.simpleName} finished and removed for chatId: $chatId")
        } catch (e: NullPointerException) {
            log.error("[ERROR] Ability with chatId: $chatId is not found", e)
        }
    }

    override fun abilityAction(chatId: Long, actionData: Any?) {
        try {
            val ability = abilityMap[chatId]!!
            ability.action(actionData)
            log.info("[INFO] ${ability.javaClass.simpleName} action for chatId: $chatId")
        } catch (e: NullPointerException) {
            log.error("[ERROR] Ability with chatId: $chatId is not found", e)
        }
    }
}
