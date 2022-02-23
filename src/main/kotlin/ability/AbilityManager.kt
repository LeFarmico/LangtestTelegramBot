package ability

import org.slf4j.LoggerFactory

class AbilityManager : IAbilityManager {

    private val lock = Any()
    private val log = LoggerFactory.getLogger(javaClass.simpleName)
    private val abilityMap: MutableMap<Class<out IAbility>, IAbility> = mutableMapOf()

    override fun addAbility(abilityClass: Class<out IAbility>, ability: IAbility) {
        synchronized(lock) {
            if (abilityMap[abilityClass] == null) {
                log.info("[INFO] ${ability.javaClass.simpleName} added to Ability Manager")
                abilityMap[abilityClass] = ability
            } else {
                log.warn("[WARN] ${ability.javaClass.simpleName} already exist.")
            }
        }
    }

    override fun removeAbility(abilityClass: Class<out IAbility>) {
        try {
            synchronized(lock) {
                abilityMap.remove(abilityClass)
            }
            log.info("[INFO] ${abilityClass.javaClass.simpleName} has removed for.")
        } catch (e: NullPointerException) {
            log.error("[ERROR] ${abilityClass.javaClass.simpleName} was not found", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : IAbility> getAbility(abilityClass: Class<T>): T? {
        return try {
            synchronized(lock) {
                abilityMap[abilityClass] as T
            }
        } catch (e: TypeCastException) {
            log.error("${abilityClass.simpleName} must implements IAbility interface.", e)
            null
        }
    }
}
