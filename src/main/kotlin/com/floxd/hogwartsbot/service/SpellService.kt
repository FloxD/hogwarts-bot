package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.SpellEnum
import com.floxd.hogwartsbot.entity.Spell
import com.floxd.hogwartsbot.repository.SpellRepository
import net.dv8tion.jda.api.entities.Member
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional
import kotlin.random.Random

@Service
class SpellService(val spellRepository: SpellRepository, val effectService: EffectService, val userService: UserService) {

    @Transactional
    fun learnSpell(member: Member, spell: SpellEnum): String {
        val user = userService.getUser(member.id) ?: userService.addUser(member)

        if (user.spells.any { it.name == spell }) {
            return "You have already learned ${spell.spellName}."
        }

        if (user.exp < spell.cost) {
            return "You don't have enough exp to learn ${spell.spellName}.\n" +
                    "You have ${user.exp}xp but you need at least ${spell.cost}."
        }

        spellRepository.save(Spell(Random.nextLong(), spell, user, 0, LocalDateTime.now().minusYears(1)))

        user.exp -= spell.cost
        userService.updateUser(user)

        return "You have learned ${spell.spellName}! Use it responsibly."
    }

    @Transactional
    fun castSpell(spell: SpellEnum, caster: Member, target: Member): CastingResult {
        val casterUser = userService.getUser(caster.id) ?: userService.addUser(caster)
        val targetUser = userService.getUser(target.id) ?: userService.addUser(target)

        val casterSpell = casterUser.spells.find { it.name == spell }

        // check if the caster has learned the spell already
        if (casterSpell == null) {
            return CastingResult.FAILED
        }

        if (casterSpell.lastCast.plusHours(spell.cooldownInHours).isAfter(LocalDateTime.now())) {
            return CastingResult.COOLDOWN
        }

        effectService.checkForEffect(spell, target.id).let {
            if (it.isAffected) {
                return CastingResult.ALREADY_AFFECTED
            }
            // check if there's already a record of the effect in DB to update it or add one
            if (it.effectRecord != null) {
                it.effectRecord.lastApplied = LocalDateTime.now()
                // TODO the duration could be changed when implementing leveling
                it.effectRecord.durationInHours = spell.durationInHours
                effectService.saveEffect(it.effectRecord)
                spellRepository.updateSpellLastCast(spell, casterUser.id, LocalDateTime.now())
                return CastingResult.SUCCESS
            } else {
                effectService.addEffect(targetUser, spell, spell.durationInHours)
                spellRepository.updateSpellLastCast(spell, casterUser.id, LocalDateTime.now())
                return CastingResult.SUCCESS
            }
        }
    }

    @Transactional
    fun updateLastCast(spell: SpellEnum, userId: Long, lastCast: LocalDateTime) {
        spellRepository.updateSpellLastCast(spell, userId, lastCast)
    }

    enum class CastingResult {
        SUCCESS, ALREADY_AFFECTED, COOLDOWN, FAILED;
    }

}
