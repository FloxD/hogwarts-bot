package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.exception.BotException
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
class AddPointsCommand(val houseService: HouseService) : Command() {
    override fun commandName(): String {
        return "addpoints"
    }

    override fun needsModPermissions(): Boolean {
        return true
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("addpoints", "add points to house")
            .addOption(OptionType.STRING, "house", "Gryffindor, Hufflepuff, Ravenclaw or Slytherin")
            .addOption(OptionType.INTEGER, "points", "how many points you want to add to the house")
            .addOption(OptionType.USER, "user", "add points to the house the user belongs to")
            .addOption(
                OptionType.STRING,
                "message",
                "an optional message about why points were added (tip: start the message with 'for ...')"
            )
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        val userOption = event.getOption("user")
        val houseOption = event.getOption("house")
        val pointsToAddOption = event.getOption("points") ?: throw BotException("Points must be set")
        val messageOption = event.getOption("message")

        if (userOption != null) {
            val message = houseService.addPointsUser(event.member, userOption, pointsToAddOption, messageOption)
            return MessageEmbedFactory.create("Added points", message)
        } else if (houseOption != null) {
            val message = houseService.addPointsHouse(event.member, houseOption, pointsToAddOption, messageOption)
            return MessageEmbedFactory.create("Added points", message)
        } else {
            throw BotException("Either a house or user must be selected")
        }
    }

    override fun twitchCommand(message: TwitchMessage): String {
        val command = message.message.split(" ")
        val house = command[1]
        val points = command[2]
        return houseService.addPointsHouseTwitch(house, points.toInt())
    }
}
