package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.model.TwitchMessage
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service
import javax.transaction.NotSupportedException

@Service
class LearnCommand : Command() {
    override fun commandName(): String {
        return "learn"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("learn", "learn a new spell")
                .addOption(OptionType.STRING, "spell", "Chose the spell to learn. Available spells: Expelliarmus")
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        TODO("Not yet implemented")
    }

    override fun twitchCommand(message: TwitchMessage): String {
        throw NotSupportedException()
    }
}
