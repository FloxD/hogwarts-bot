package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.entity.User
import com.floxd.hogwartsbot.repository.UserRepository
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional

@Service
class UserService(val userRepository: UserRepository) {

    private val RANDOM = Random()

    @Transactional
    fun practiceMagic(member: Member?): String {
        if (member == null) {
            throw Exception("Didn't provide user - this shouldn't have happend.")
        }

        val user = userRepository.findByDiscordId(member.id)

        user?.let {
            if (user.lastExp.plusHours(12).isAfter(LocalDateTime.now())) {
                return "You need to wait 12 hrs between practicing magic"
            }

            val experienceToAdd = RANDOM.nextLong(10)
            userRepository.addExperience(member.id, experienceToAdd)
            userRepository.updateLastExp(member.id, LocalDateTime.now())
            return "You gained ${experienceToAdd}xp! You now have ${user.exp + experienceToAdd}xp in total!"
        } ?: run {
            addUser(member)

            val experienceToAdd = RANDOM.nextLong(10)
            userRepository.addExperience(member.id, experienceToAdd)
            userRepository.updateLastExp(member.id, LocalDateTime.now())

            return "You gained ${experienceToAdd}xp! You now have ${experienceToAdd}xp in total!"
        }
    }

    fun exp(userOption: OptionMapping): String {
        val asMember = userOption.asMember
        if (asMember == null) {
            throw Exception("Didn't provide user - this shouldn't have happend.")
        }

        val userId = asMember.id
        val user = userRepository.findByDiscordId(userId)

        user?.let {
            return "${user.discordName} has ${user.exp}xp"
        } ?: run {
            addUser(asMember)
            return "${asMember.effectiveName} has 0xp"
        }
    }

    fun exp(member: Member?): String {
        if (member == null) {
            throw Exception("Didn't provide user - this shouldn't have happend.")
        }

        val user = userRepository.findByDiscordId(member.id)

        user?.let {
            return "You have ${user.exp}xp"
        } ?: run {
            addUser(member)
            return "You have 0xp"
        }
    }

    fun leaderBoard(): String {
        val leaderboard = userRepository.leaderboard()

        val message = leaderboard
            .take(10)
            .mapIndexed { index: Int, user: User -> "${index + 1}: ${user.discordName} - ${user.exp}xp" }
            .joinToString("\n")

        return "Exp Leaderboard:\n" + message
    }

    private fun addUser(member: Member) {
        userRepository.save(
            User(RANDOM.nextLong(), member.id, member.effectiveName, 0, LocalDateTime.now().minusDays(10))
        )
    }
}
