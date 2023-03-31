package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.SpellEnum
import com.floxd.hogwartsbot.entity.Effect
import com.floxd.hogwartsbot.entity.User
import com.floxd.hogwartsbot.repository.EffectRepository
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class EffectService(val effectRepository: EffectRepository) {

    fun checkForEffect(spell: SpellEnum, discordId: String): EffectResult {
        getEffect(spell, discordId)?.let {
            //check duration
            val effectEndsAt = it.lastApplied.plusHours(it.durationInHours)
            if (effectEndsAt.isAfter(LocalDateTime.now()))
                return EffectResult(true, Duration.between(LocalDateTime.now(), effectEndsAt), it)
            else
                return EffectResult(false, Duration.ZERO, it)
        }
        return EffectResult(false, Duration.ZERO, null)
    }

    fun getEffect(spell: SpellEnum, discordId: String): Effect? {
        return effectRepository.findByDiscordIdAndSpell(discordId, spell)
    }

    fun addEffect(user: User, spell: SpellEnum, durationInHours: Long): Effect {
        return effectRepository.save(Effect(Random.nextLong(), spell, user, LocalDateTime.now(), durationInHours))
    }

    fun saveEffect(effect: Effect): Effect {
        return effectRepository.save(effect)
    }
}

class EffectResult(val isAffected: Boolean, val timeLeft: Duration, val effectRecord: Effect?)
