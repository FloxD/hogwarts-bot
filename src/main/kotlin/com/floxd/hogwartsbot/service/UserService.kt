package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.entity.User
import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.repository.UserRepository
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional

@Service
class UserService(val userRepository: UserRepository) {

    private val RANDOM = Random()
    private val PRACTICE_MAGIC_WAIT_HOURS: Long = 12

    @Transactional
    fun practiceMagic(member: Member?): String {
        if (member == null) {
            throw BotException("Didn't provide user - this shouldn't have happend.")
        }

        val user = userRepository.findByDiscordId(member.id)

        user?.let {
            if (user.lastExp.plusHours(PRACTICE_MAGIC_WAIT_HOURS).isAfter(LocalDateTime.now())) {
                val between = Duration.between(LocalDateTime.now(), user.lastExp.plusHours(PRACTICE_MAGIC_WAIT_HOURS))
                return "You need to wait ${between.toHoursPart()} hrs ${between.toMinutesPart()} mins until you can practice magic again"
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
            throw BotException("Didn't provide user - this shouldn't have happend.")
        }

        val userId = asMember.id
        val user = userRepository.findByDiscordId(userId)

        user?.let {
            return "<@${user.discordId}> has ${user.exp}xp"
        } ?: run {
            addUser(asMember)
            return "${asMember.effectiveName} has 0xp"
        }
    }

    fun exp(member: Member?): String {

        if (member == null) {
            throw BotException("Didn't provide user - this shouldn't have happend.")
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
                .mapIndexed { index: Int, user: User -> "${index + 1}: <@${user.discordId}> - ${user.exp}xp" }
                .joinToString("\n")

        return message
    }

    fun getUser(discordId: String): User? {
        return userRepository.findByDiscordId(discordId)
    }

    fun addUser(member: Member): User {
        return userRepository.save(
                User(RANDOM.nextLong(), member.id, null, 0, LocalDateTime.now().minusYears(1))
        )
    }

    fun updateUser(user: User): User {
        return userRepository.save(user)
    }
}
