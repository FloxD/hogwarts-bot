package com.floxd.hogwartsbot.entity

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
open class Audit(@Id var id: Long,
                 var timestamp: LocalDateTime,
                 var name: String,
                 var points: Long,
                 var username: String,
                 var userid: Long) {
    constructor() : this(0, LocalDateTime.now(), "Undefined", 0, "Undefined", 0)
}
