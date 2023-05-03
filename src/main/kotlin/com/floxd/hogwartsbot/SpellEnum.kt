package com.floxd.hogwartsbot


/**
 * @param spellName = name of spell
 * @param durationInHours = how long the spell is active in hours
 * @param cooldownInHours = the time the caster has to wait until they can cast this spell again
 * @param cost = how much xp it costs to learn the spell
 */
enum class SpellEnum(val spellName: String, val durationInHours: Long, val cooldownInHours: Long, val cost: Long) {
    NO_CHARM("empty", 0, 0, 0),
    EXPELLIARMUS("Expelliarmus", 30, 48, 50),
    PROTEGO("Protego", 12, 16, 75),
    INCENDIO("Incendio", 1, 12, 50)
}
