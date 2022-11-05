package com.floxd.hogwartsbot.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
open class House(@Id var id: Long,
                 var name: String,
                 var points: Long) {
    constructor() : this(0, "Undefined", 0)
}
