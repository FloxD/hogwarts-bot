package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.entity.Audit
import com.floxd.hogwartsbot.entity.House
import com.floxd.hogwartsbot.repository.AuditRepository
import com.floxd.hogwartsbot.repository.HouseRepository
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class CommandListener(val houseRepository: HouseRepository, val auditRepository: AuditRepository) : ListenerAdapter() {

    private val USER_ID_ELINA = "233607037232218112"
    private val USER_ID_FLOXD = "132602254531362817"
    private val USER_ID_SMART = "897174957086343218"
    private val MODS = setOf(USER_ID_ELINA, USER_ID_FLOXD, USER_ID_SMART)
    private val RANDOM = Random()

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "points" -> {
                var house = event.getOption("house")?.asString ?: run {
                    event.reply("You must select a house").setEphemeral(true).queue()
                    return
                }

                // set first letter to uppercase in case it's lowercase
                if (house[0].isLowerCase()) {
                    house = house[0].uppercaseChar() + house.substring(1)
                }
                val findByName = houseRepository.findByName(house)
                findByName.ifPresent { event.reply("House ${it.name} has ${it.points} points.").queue() }
            }

            "addpoints" -> {
                val userId = event.member?.id ?: return

                if (!MODS.contains(userId)) {
                    event.reply("You're not allowed to add points").setEphemeral(true).queue()
                    return
                }

                var house = event.getOption("house")?.asString ?: run {
                    event.reply("You must select a house").setEphemeral(true).queue()
                    return
                }

                val pointsToAdd = event.getOption("points")?.asInt ?: run {
                    event.reply("You must set how many points you want to add").setEphemeral(true).queue()
                    return
                }

                // set first letter to uppercase in case it's lowercase
                if (house[0].isLowerCase()) {
                    house = house[0].uppercaseChar() + house.substring(1)
                }

                val findByName = houseRepository.findByName(house)
                findByName.ifPresentOrElse({
                    houseRepository.save(House(it.id, it.name, it.points + pointsToAdd))
                    auditRepository.save(
                        Audit(
                            RANDOM.nextLong(),
                            LocalDateTime.now(),
                            it.name,
                            pointsToAdd.toLong(),
                            event.member?.user?.name ?: "Undefined",
                            userId.toLong()
                        )
                    )
                    val message = event.getOption("message")?.asString
                    val firstLine = "Added $pointsToAdd to ${house}${message?.let { ' ' + message } ?: run { "" }}!"
                    val secondLine = "$house has now ${it.points + pointsToAdd} points in total"
                    event.reply("$firstLine\n$secondLine").queue()
                }, {
                    event.reply("House $house does not exist").setEphemeral(true).queue()
                })
            }

            "ping" -> {
                val time = System.currentTimeMillis()
                event.reply("Pong!")
                    .setEphemeral(true) // reply or acknowledge
                    .flatMap { v -> event.hook.editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) }
                    .queue()
            }
        }
    }
}
