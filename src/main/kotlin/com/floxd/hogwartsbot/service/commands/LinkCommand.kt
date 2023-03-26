package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.model.TwitchMessage
import com.floxd.hogwartsbot.service.LinkService
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service

@Service
class LinkCommand(val linkService: LinkService) : Command() {
    override fun commandName(): String {
        return "link"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("link", "to link your discord and twitch account")
            .addOption(OptionType.STRING, "username", "Twitch Username")
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        val twitchUsernameOption = event.getOption("username")

        if (twitchUsernameOption != null) {
            val message =
                linkService.linkDiscord(event.member?.id?.lowercase() ?: "", twitchUsernameOption.asString.lowercase())
            return MessageEmbedFactory.create("Linking Request", message)
        } else {
            throw BotException("You need to set a twitch username")
        }
    }

    override fun twitchCommand(message: TwitchMessage): String {
        val command = message.message.split(" ")
        val discordId = command[1]
        return linkService.linkTwitch(message.username.lowercase(), discordId.lowercase())
    }
}
