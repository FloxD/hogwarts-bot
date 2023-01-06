package com.floxd.hogwartsbot.repository

import com.floxd.hogwartsbot.entity.User
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserRepository : CrudRepository<User, Long> {

    fun findByDiscordId(discordId: String): User?

    @Modifying
    @Query("update User u set u.exp = u.exp + ?2 where u.discordId = ?1")
    fun addExperience(discordId: String, expToAdd: Long)

    @Modifying
    @Query("update User u set u.lastExp = ?2 where u.discordId = ?1")
    fun updateLastExp(discordId: String, lastExp: LocalDateTime)

    @Query("select new User(u.id, u.discordId, u.discordName, u.exp, u.lastExp) from User u order by u.exp desc")
    fun leaderboard(): List<User>
}
