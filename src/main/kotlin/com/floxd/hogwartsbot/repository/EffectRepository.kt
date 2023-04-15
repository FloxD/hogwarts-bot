package com.floxd.hogwartsbot.repository

import com.floxd.hogwartsbot.SpellEnum
import com.floxd.hogwartsbot.entity.Effect
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EffectRepository : CrudRepository<Effect, Long> {
    @Query("select e from Effect e where e.user.discordId = ?1 and e.spell = ?2")
    fun findByDiscordIdAndSpell(discordId: String, spell: SpellEnum): Effect?
}
