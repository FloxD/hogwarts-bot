package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.model.TwitchMessage
import com.floxd.hogwartsbot.service.UserService
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service

@Service
class ExperienceCommand(val userService: UserService) : Command() {
    override fun commandName(): String {
        return "exp"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("exp", "to see how much exp you have")
            .addOption(OptionType.USER, "user", "optionally specify a different user than your own")
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        val userOption = event.getOption("user")

        if (userOption != null) {
            val message = userService.exp(userOption)
            return MessageEmbedFactory.create("Current Experience", message)
        } else {
            val message = userService.exp(event.member)
            return MessageEmbedFactory.create("Current Experience Points", message)
        }
    }

    override fun twitchCommand(message: TwitchMessage): String {
        throw UnsupportedOperationException()
    }
}
