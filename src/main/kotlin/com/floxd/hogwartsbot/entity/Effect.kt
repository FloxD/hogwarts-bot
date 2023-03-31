package com.floxd.hogwartsbot.entity

import com.floxd.hogwartsbot.SpellEnum
import java.time.LocalDateTime
import javax.persistence.*

/**
 * id: internal id
 * spell: the name of the spell
 * user: The name of user that is affected
 * lastApplied: timestamp when the effect was applied
 * duraationInHours: how long the effect lasts in hours
 */
@Table
@Entity
class Effect(
        @Id
        var id: Long,
        @Enumerated(EnumType.STRING)
        var spell: SpellEnum,
        @ManyToOne
        var user: User,
        var lastApplied: LocalDateTime,
        var durationInHours: Long
) {
    constructor() : this(0, SpellEnum.NO_CHARM, User(), LocalDateTime.MIN, 0)
}
