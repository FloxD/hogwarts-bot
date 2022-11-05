package com.floxd.hogwartsbot.service

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import java.util.*

@Component
class CommandListener(val houseService: HouseService) : ListenerAdapter() {

    private val USER_ID_FLOXD = "132602254531362817"
    private val MOD_GROUP_ID = "481726205603741696"
    private val MOD_USER_IDS = setOf(USER_ID_FLOXD)
    private val MOD_GROUP_IDS = setOf(MOD_GROUP_ID)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        try {
            when (event.name) {
                "points" -> {
                    val houseOption = event.getOption("house")

                    houseOption?.let {
                        event.reply(houseService.getPoints(it)).queue()
                    } ?: run {
                        event.reply(houseService.getAllPoints()).queue()
                    }
                }

                "addpoints" -> {
                    hasModPrivileges(event.member)
                    val userOption = event.getOption("user")
                    val houseOption = event.getOption("house")
                    val pointsToAddOption = event.getOption("points") ?: throw Exception("Points must be set")
                    val messageOption = event.getOption("message")

                    if (userOption != null) {
                        val message = houseService.addPointsUser(event.member, userOption, pointsToAddOption, messageOption)
                        event.reply(message).queue()
                    } else if (houseOption != null) {
                        val message = houseService.addPointsHouse(event.member, houseOption, pointsToAddOption, messageOption)
                        event.reply(message).queue()
                    } else {
                        throw Exception("Either a house or user must be selected")
                    }
                }

                "subtractpoints" -> {
                    hasModPrivileges(event.member)
                    val userOption = event.getOption("user")
                    val houseOption = event.getOption("house")
                    val pointsToAddOption = event.getOption("points") ?: throw Exception("Points must be set")
                    val messageOption = event.getOption("message")

                    if (userOption != null) {
                        val message = houseService.subtractPointsUser(event.member, userOption, pointsToAddOption, messageOption)
                        event.reply(message).queue()
                    } else if (houseOption != null) {
                        val message = houseService.subtractPointsHouse(event.member, houseOption, pointsToAddOption, messageOption)
                        event.reply(message).queue()
                    } else {
                        throw Exception("Either a house or user must be selected")
                    }
                }

                "ping" -> {
                    val time = System.currentTimeMillis()
                    event.reply("Pong!")
                        .setEphemeral(true)
                        .flatMap { v ->
                            event.hook.editOriginalFormat(
                                "Pong: %d ms",
                                System.currentTimeMillis() - time
                            )
                        }
                        .queue()
                }
            }
        } catch (e: Exception) {
            event.reply(e.message ?: "unknown error happened").setEphemeral(true).queue()
        }
    }

    private fun hasModPrivileges(user: Member?): Boolean {
        val id = user?.user?.id ?: return false

        if (MOD_USER_IDS.contains(id)) {
            return true
        }

        val groupIds = user.roles

        for (groupId in groupIds) {
            if (MOD_GROUP_IDS.contains(groupId.name)) {
                return true
            }
        }

        return false
    }
}
