package com.floxd.hogwartsbot;

//Warning: The value of id is used in EffectRepository. Do NOT change it after populating the database with the spell.
/**
 *@param id
 *A unique id of the spell.
 *@param name
 *Name of the spell.
 *@param durationInHours
 *How many hours does the effect last.
 *@param minExp
 *The minimum amount of exp. points the user needs to cast the spell.
 *@param perfectExp
 *The amount of exp. points the user needs to cast the spell without a chance of a backfire.
*/
enum class SpellEnum(val id :Long,  val durationInHours :Long, val minExp :Long, val perfectExp :Long ) {
    EXPELLIARMUS( 1, 12, 350, 600)

}
