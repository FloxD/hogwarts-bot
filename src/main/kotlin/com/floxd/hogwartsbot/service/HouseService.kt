package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.HouseEnum
import com.floxd.hogwartsbot.entity.Audit
import com.floxd.hogwartsbot.entity.House
import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.repository.AuditRepository
import com.floxd.hogwartsbot.repository.HouseRepository
import com.floxd.hogwartsbot.toNullable
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class HouseService(val houseRepository: HouseRepository,
                   val auditRepository: AuditRepository) {

    private val RANDOM = Random()

    /**
     * fetches all houses and returns the message to be sent in discord
     *
     * @return the message to be sent to discord
     */
    fun getAllPoints(): String {
        val houses = houseRepository.findAll()
        if (houses.count() == 0) { throw BotException("No houses found, this should not have happened") }
        return houses.map { "${it.name} has ${it.points} points.\n" }.joinToString("", "", "")
    }

    /**
     * fetches the points of the house the user chose
     *
     * @return the message to be sent to discord
     */
    @Throws(BotException::class)
    fun getPoints(houseOption: OptionMapping): String {

        val house = houseOption.asString
        validateHouse(house)
        val normalizedHouse = normalizeHouse(house)
        val findByName = houseRepository.findByName(normalizedHouse).toNullable()

        findByName?.let {
            return "${it.name} has ${it.points} points."
        } ?: run {
            throw BotException("Couldn't find house ${normalizedHouse} in the database")
        }
    }

    /**
     * TODO better naming
     */
    @Throws(BotException::class)
    fun addPointsHouse(member: Member?, houseOption: OptionMapping, pointsOption: OptionMapping, messageOption: OptionMapping?): String {
        val house = houseOption.asString
        validateHouse(house)
        val normalizedHouse = normalizeHouse(house)

        val pointsToAdd = pointsOption.asInt

        if (pointsToAdd <= 0) {
            throw BotException("Points to add must be > 0.")
        }

        val findByName = houseRepository.findByName(normalizedHouse).toNullable()
        findByName?.let {
            houseRepository.save(House(it.id, it.name, it.points + pointsToAdd))
            saveAudit(it, pointsToAdd, member)

            val message = messageOption?.asString
            val firstLine = "Added $pointsToAdd to ${it.name}${message?.let { ' ' + message } ?: run { "" }}!"
            val secondLine = "${it.name} has now ${it.points + pointsToAdd} points in total"
            return "$firstLine\n$secondLine"
        } ?: run {
            throw BotException("House starting with $normalizedHouse does not exist")
        }
    }

    /**
     * TODO better naming
     */
    @Throws(BotException::class)
    fun addPointsUser(member: Member?, userOption: OptionMapping, pointsOption: OptionMapping, messageOption: OptionMapping?): String {
        val pointsToAdd = pointsOption.asInt

        if (pointsToAdd <= 0) {
            throw BotException("Points to add must be > 0.")
        }

        // fetch the user's house
        val userHouse = userOption.asMember?.roles?.filter {
            HouseEnum.values()
                .map { it.houseName }
                .contains(it.name)
        }?.first()?.name ?: throw BotException("User has no house role.")

        val findByName = houseRepository.findByName(userHouse).toNullable()
        findByName?.let {
            houseRepository.save(House(it.id, it.name, it.points + pointsToAdd))
            saveAudit(it, pointsToAdd, member)

            val message = messageOption?.asString
            val firstLine = "Added $pointsToAdd to ${userHouse}${message?.let { ' ' + message } ?: run { "" }}!"
            val secondLine = "$userHouse has now ${it.points + pointsToAdd} points in total"
            return "$firstLine\n$secondLine"
        } ?: run {
            throw BotException("House $userHouse does not exist")
        }
    }

    @Throws(BotException::class)
    fun subtractPointsHouse(member: Member?, houseOption: OptionMapping, pointsOption: OptionMapping, messageOption: OptionMapping?): String {
        val house = houseOption.asString
        validateHouse(house)
        val normalizedHouse = normalizeHouse(house)

        val pointsToSubtract = pointsOption.asInt

        if (pointsToSubtract <= 0) {
            throw BotException("Points to subtract must be > 0.")
        }

        val findByName = houseRepository.findByName(normalizedHouse).toNullable()
        findByName?.let {
            houseRepository.save(House(it.id, it.name, it.points - pointsToSubtract))
            saveAudit(it, pointsToSubtract * -1, member)

            val message = messageOption?.asString
            val firstLine = "Subtracted $pointsToSubtract from ${it.name}${message?.let { ' ' + message } ?: run { "" }}!"
            val secondLine = "${it.name} has now ${it.points - pointsToSubtract} points in total"
            return "$firstLine\n$secondLine"
        } ?: run {
            throw BotException("House starting with $normalizedHouse does not exist")
        }
    }

    @Throws(BotException::class)
    fun subtractPointsUser(member: Member?, userOption: OptionMapping, pointsOption: OptionMapping, messageOption: OptionMapping?): String {
        val pointsToSubtract = pointsOption.asInt

        if (pointsToSubtract <= 0) {
            throw BotException("Points to subtract must be > 0.")
        }

        // fetch the user's house
        val userHouse = userOption.asMember?.roles?.filter {
            HouseEnum.values()
                .map { it.houseName }
                .contains(it.name)
        }?.first()?.name ?: throw BotException("User has no house role.")

        val findByName = houseRepository.findByName(userHouse).toNullable()
        findByName?.let {
            houseRepository.save(House(it.id, it.name, it.points - pointsToSubtract))
            saveAudit(it, pointsToSubtract * -1, member)

            val message = messageOption?.asString
            val firstLine = "Subtracted $pointsToSubtract from ${userHouse}${message?.let { ' ' + message } ?: run { "" }}!"
            val secondLine = "$userHouse has now ${it.points - pointsToSubtract} points in total"
            return "$firstLine\n$secondLine"
        } ?: run {
            throw BotException("House $userHouse does not exist")
        }
    }

    private fun validateHouse(house: String) {
        if (house.isBlank()) {
            throw BotException("The value you provided for house was blank.")
        }
    }

    /**
     * makes sure that the house which the user specifies starts with a uppercase letter and with the rest in lowercase
     * e.g.:
     * input      | output
     * Gryffindor | Gryffindor
     * gryffindor | Gryffindor
     * gRyFfInDoR | Gryffindor
     * gryff      | Gryff
     */
    private fun normalizeHouse(unnormalizedHouseString: String): String {
        if (unnormalizedHouseString.length == 1) {
            return unnormalizedHouseString[0].uppercaseChar().toString()
        }

        // set first letter to uppercase in case it's lowercase and add the rest in lowercase
        return unnormalizedHouseString[0].uppercaseChar() + unnormalizedHouseString.lowercase().substring(1)
    }

    private fun saveAudit(house: House,
                          pointsToAdd: Int,
                          member: Member?) {
        auditRepository.save(
            Audit(
                RANDOM.nextLong(),
                LocalDateTime.now(),
                house.name,
                pointsToAdd.toLong(),
                member?.user?.name ?: "Undefined",
                member?.id?.toLong() ?: throw BotException("User id is not set in event. This should not happen.")
            )
        )
    }
}
