package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.model.TwitchMessage
import com.floxd.hogwartsbot.service.UserService
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service

@Service
class PracticeMagicCommand(val userService: UserService) : Command() {
    override fun commandName(): String {
        return "practicemagic"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("practicemagic", "practice your magic. you can practice every 12 hours")
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        return MessageEmbedFactory.create("Practiced Magic", userService.practiceMagic(event.member))
    }

    override fun twitchCommand(message: TwitchMessage): String {
        throw UnsupportedOperationException()
    }
}
