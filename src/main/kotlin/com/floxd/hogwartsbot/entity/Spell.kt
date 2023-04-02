package com.floxd.hogwartsbot.entity

import com.floxd.hogwartsbot.SpellEnum
import javax.persistence.*

/**
 * id = row id
 *
 */
@Entity
@Table
open class Spell(@Id var id: Long,
                 var name: SpellEnum,
                 var level: Long) {


    @ManyToMany(mappedBy = "users")
    @JoinTable(
            name = "user_spell",
            joinColumns = [JoinColumn(name = "spell_id")],
            inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    var users: MutableSet<User> = mutableSetOf()

    constructor() : this(0, SpellEnum.EXPELLIARMUS, 0)
}
