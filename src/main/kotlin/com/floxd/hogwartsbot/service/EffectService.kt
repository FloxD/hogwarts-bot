package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.SpellEnum
import com.floxd.hogwartsbot.entity.Effect
import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.repository.EffectRepository
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import javax.transaction.Transactional
import kotlin.random.Random

@Service
class EffectService(val effectRepository: EffectRepository) {

    fun castExpelliarmus(targetOption: OptionMapping?, casterMember: Member?, exp :Long) :String{
        checkForExpelliarmusEffect(casterMember)?.let { return it }
        val targetUserName = getDiscordName(targetOption)
        return when(castSpell(SpellEnum.EXPELLIARMUS, getDiscordId(targetOption), getDiscordId(casterMember),exp)){
            CastingResultEnum.SUCCESS -> "*$targetUserName*'s wand flew off. They'll get it back in ${SpellEnum.EXPELLIARMUS.durationInHours} hours."
            CastingResultEnum.BACKFIRED -> "The spell backfired! Your wand flew off. you'll get it back in ${SpellEnum.EXPELLIARMUS.durationInHours} hours."
            CastingResultEnum.ALREADY_AFFECTED -> "*$targetUserName* is already disarmed and can't use magic for now."
        }
    }

    /**Should be used in every spell function to check if the caster is under 'Expelliarmus' effect
     * @return  if under the effect, a text to use as a MessageEmbed.description. otherwise it returns null.
     */
    fun checkForExpelliarmusEffect(member: Member?) :String?{
        val effect = getEffect(SpellEnum.EXPELLIARMUS, getDiscordId(member))
        if(effect != null){
            timeLeftUntilEffectEnds(effect)?.let{
                return "Someone has casted *Expelliarmus* on you. You'll get your wand back in ${it.toHoursPart()}h, ${it.toMinutesPart()}m"
            }
        }
        return null
    }

    /**
     * @return the duration left until the effect ends. If it has already ended returns null.
    */
    fun timeLeftUntilEffectEnds(effect: Effect) : Duration?{
        val effectEndsAt= effect.lastCast.plusHours(getSpellById(effect.spellId).durationInHours)
        if(effectEndsAt.isAfter(LocalDateTime.now()))
            return Duration.between(LocalDateTime.now(), effectEndsAt)
        else
            return null
    }

    fun getEffect(spell :SpellEnum, discordId :String) :Effect?{
        return effectRepository.findByDiscordIdAndSpellId(discordId, spell.id)
    }

    @Transactional
    private fun castSpell(spell: SpellEnum, targetId: String, casterId: String, exp :Long) : CastingResultEnum {
        //Check if target has record for this effect in database. update it or add one if doesn't exist
        getEffect(spell, targetId)?.let {
            if (timeLeftUntilEffectEnds(it) != null)
                return CastingResultEnum.ALREADY_AFFECTED
            else {
                val targetEffect :Effect = it
                if (backfired(exp, spell)) {
                    //The spell backfired
                    //Check if caster has record for this effect in database. update it or add one if doesn't exist
                    val casterEffect :Effect? = getEffect(spell, casterId)
                    if(casterEffect != null){
                        casterEffect.lastCast = LocalDateTime.now()
                        effectRepository.save(casterEffect)
                    }
                    else{
                        addEffect(casterId, spell)
                    }
                    return CastingResultEnum.BACKFIRED
                } else {
                    targetEffect.lastCast = LocalDateTime.now()
                    effectRepository.save(targetEffect)
                    return CastingResultEnum.SUCCESS
                }
            }
        } ?: run {
            addEffect(targetId, spell)
            return CastingResultEnum.SUCCESS
        }
    }

    private enum class CastingResultEnum {
        SUCCESS,ALREADY_AFFECTED,BACKFIRED;
    }

    private fun backfired(exp: Long, spell: SpellEnum) :Boolean{
        if (exp >= spell.perfectExp)
            return false
        val diceToBeat = Random.nextLong(spell.minExp, spell.perfectExp)
        return exp <= diceToBeat
    }

    private fun addEffect(discordId :String, spell: SpellEnum) :Effect{
        val e = Effect(Random.nextLong(),discordId,spell.id, LocalDateTime.now())
        effectRepository.save(e)
        return e
    }

    @Throws(BotException::class)
    private fun getSpellById(id :Long): SpellEnum{
        val spell = SpellEnum.values().firstOrNull { it.id==id }
        if(spell != null){
            return spell
        }
        else
            throw BotException("Something is wrong with the bot. - enum not found.")
    }

    @Throws(BotException::class)
    private fun getDiscordId(member: Member?) :String{
        if (member == null) {
            throw BotException("Something is wrong with the bot. - member is null.")
        }
            return member.id
    }

    @Throws(BotException::class)
    private fun getDiscordId(option: OptionMapping?) :String{
        if (option == null) {
            throw BotException("Something went wrong. - option is null.")
        }
        val member = option.asMember ?: throw BotException("Something is wrong with the bot. - member is null.")
        return member.id
    }
    @Throws(BotException::class)
    private fun getDiscordName(option: OptionMapping?) :String{
        if (option == null) {
            throw BotException("Something went wrong. - option is null.")
        }
        return option.asUser.name
    }
}