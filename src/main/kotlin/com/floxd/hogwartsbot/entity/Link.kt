package com.floxd.hogwartsbot.entity

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table
@Entity
open class Link(@Id var id: Long,
                var initiator: Platform,
                var discordId: String?,
                var twitchName: String?,
                var creationDate: LocalDateTime) {

    constructor() : this(0, Platform.DISCORD, null, null, LocalDateTime.MIN)
}

enum class Platform(val platformName: String) {
    DISCORD("Discord"),
    TWITCH("Twitch")
}
