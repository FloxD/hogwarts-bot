package com.floxd.hogwartsbot;

//Warning: The value of id is used in EffectRepository. Do NOT change it after populating the database with the spell.
/**
 * spellName = name of spell
 * durationInHours = how long the spell is active in hours
 * cost = how much xp it costs to learn the spell
 */
enum class SpellEnum(val spellName: String, val durationInHours: Long, val cost: Long) {
    EXPELLIARMUS("Expelliarmus", 12, 50)
}
