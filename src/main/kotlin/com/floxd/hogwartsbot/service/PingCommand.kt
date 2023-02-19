package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.model.TwitchMessage
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service

@Service
class PingCommand : Command() {
    override fun commandName(): String {
        return "ping"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("ping", "check ping")
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        return MessageEmbedFactory.create("Pong", "Stinky Poopy")
    }

    override fun twitchCommand(message: TwitchMessage): String {
        return "Pong"
    }
}
