package com.floxd.hogwartsbot.repository

import com.floxd.hogwartsbot.SpellEnum
import com.floxd.hogwartsbot.entity.Spell
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SpellRepository : CrudRepository<Spell, Long> {
    @Modifying
    @Query("update Spell s set s.lastCast = ?3 where s.name = ?1 and s.user.id = ?2")
    fun updateSpellLastCast(spell: SpellEnum, userId: Long, lastCast: LocalDateTime)
}
