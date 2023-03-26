package com.floxd.hogwartsbot.repository

import com.floxd.hogwartsbot.entity.Link
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LinkRepository : CrudRepository<Link, Long> {

    fun getLinkByTwitchName(twitchName: String): Link?
    fun getLinkByDiscordId(discordId: String): Link?

}
