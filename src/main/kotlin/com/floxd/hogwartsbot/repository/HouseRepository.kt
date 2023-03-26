package com.floxd.hogwartsbot.repository

import com.floxd.hogwartsbot.entity.House
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface HouseRepository : CrudRepository<House, Long> {

    @Query("select new House(id, name, points) from House where name like ?1%")
    fun findByName(name: String): House?

}
