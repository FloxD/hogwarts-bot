package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.entity.House
import com.floxd.hogwartsbot.repository.HouseRepository
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component

@Component
class CommandListener(val houseRepository: HouseRepository) : ListenerAdapter() {

    private val USER_ID_ELINA = "233607037232218112"
    private val USER_ID_FLOXD = "132602254531362817"

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "points" -> {
                event.getOption("house")?.asString?.let {
                    val findByName = houseRepository.findByName(it)
                    findByName.ifPresent { event.reply("House ${it.name} has ${it.points} points.").queue() }
                }
            }

            "addpoints" -> {
                event.member?.id?.let { userId ->
                    if (USER_ID_ELINA.equals(userId) || USER_ID_FLOXD.equals(userId))
                    event.getOption("house")?.asString?.let { house ->
                        event.getOption("points")?.asInt?.let { pointsToAdd ->
                            val findByName = houseRepository.findByName(house)
                            findByName.ifPresent {
                                houseRepository.save(
                                    House(
                                        it.id,
                                        it.name,
                                        it.points + pointsToAdd
                                    )
                                )
                                event.getOption("message")?.asString?.let { message ->
                                    event.reply("Added $pointsToAdd to $house $message! $house has now ${it.points + pointsToAdd} points in total").queue()
                                } ?: run {
                                    event.reply("Added $pointsToAdd to $house! $house has now ${it.points + pointsToAdd} points in total").queue()
                                }
                            }
                        }
                    }
                } ?: run {
                    event.reply("You're not allowed to add points")
                }
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
