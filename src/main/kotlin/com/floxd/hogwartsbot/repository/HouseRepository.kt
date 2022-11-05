package com.floxd.hogwartsbot.repository

import com.floxd.hogwartsbot.entity.House
import org.springframework.data.repository.CrudRepository
import java.util.*

interface HouseRepository : CrudRepository<House, Long> {

    fun findByName(name: String): Optional<House>

}
