package com.floxd.hogwartsbot.entity

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * id: internal id
 * discordId: The discord user id
 * effectName: The name of spell or effect.
 * lastCast: A timestamp of the last time the user got affected by the spell.
 */
@Table
@Entity
class Effect (@Id var id :Long, var discordId :String, var spellId :Long, var lastCast :LocalDateTime){
    constructor() :this(0,"", 0, LocalDateTime.MIN)
}