package com.floxd.hogwartsbot.entity

import com.floxd.hogwartsbot.SpellEnum
import java.time.LocalDateTime
import javax.persistence.*

/**
 * id = row id
 * name = name of the spell
 * level = current level the spell has for the user (currently only level 0)
 * user = user mapping
 * last cast = last time the spell was casted, this is for calculating cooldown
 */
@Entity
@Table
open class Spell(@Id var id: Long,
                 @Enumerated(EnumType.STRING)
                 var name: SpellEnum,
                 @ManyToOne
                 var user: User,
                 var level: Long,
                 var lastCast: LocalDateTime) {

    constructor() : this(0, SpellEnum.EXPELLIARMUS, User(), 0, LocalDateTime.MIN)
}
