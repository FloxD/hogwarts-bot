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
class LeaderboardCommand(val userService: UserService) : Command() {
    override fun commandName(): String {
        return "leaderboard"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("leaderboard", "show top 10 users with the most exp")
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        return MessageEmbedFactory.create("Exp Leaderboard", userService.leaderBoard())
    }

    override fun twitchCommand(message: TwitchMessage): String {
        throw UnsupportedOperationException()
    }
}
