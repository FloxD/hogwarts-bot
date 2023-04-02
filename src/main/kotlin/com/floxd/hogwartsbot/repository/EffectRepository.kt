package com.floxd.hogwartsbot.repository

import com.floxd.hogwartsbot.entity.Effect
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EffectRepository :CrudRepository<Effect, Long> {
    fun findByDiscordIdAndSpellId(discordId: String, spellId :Long) :Effect?
}