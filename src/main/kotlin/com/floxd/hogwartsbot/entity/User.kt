package com.floxd.hogwartsbot.entity

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * id: internal id
 * discordId: the discord user id (usually a long, but we store in string just in case this would ever change)
 * discordName: the discord username
 * exp: current experience points
 * lastExp: timestamp when the user got his most recent daily experience with the /practicemagic command
 */
@Table
@Entity
open class User(@Id var id: Long,
                var discordId: String,
                var discordName: String,
                var exp: Long,
                var lastExp: LocalDateTime) {
    constructor() : this(0, "Undefined", "Undefined", 0, LocalDateTime.MIN) {

    }
}
