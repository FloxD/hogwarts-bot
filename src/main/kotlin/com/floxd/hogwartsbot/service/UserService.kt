package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.entity.User
import com.floxd.hogwartsbot.repository.UserRepository
import net.dv8tion.jda.api.entities.Member
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

    private fun addUser(member: Member) {
        userRepository.save(
            User(RANDOM.nextLong(), member.id, 0, LocalDateTime.now().minusDays(10))
        )
    }
}
