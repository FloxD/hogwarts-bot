package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.model.TwitchMessage
import com.floxd.hogwartsbot.service.HouseService
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service

@Service
class PointsCommand(val houseService: HouseService) : Command() {
    override fun commandName(): String {
        return "points"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("points", "view points from house")
            .addOption(OptionType.STRING, "house", "Gryffindor, Hufflepuff, Ravenclaw or Slytherin")
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        val houseOption = event.getOption("house")

        houseOption?.let {
            return MessageEmbedFactory.create("Points", houseService.getPoints(it.asString))
        } ?: run {
            return MessageEmbedFactory.create("Points", houseService.getAllPoints())
        }
    }

    override fun twitchCommand(message: TwitchMessage): String {
        val command = message.message.split(" ")
        if (command.size == 1) {
            return houseService.getAllPoints()
        } else {
            return houseService.getPoints(command[1])
        }
    }
}
