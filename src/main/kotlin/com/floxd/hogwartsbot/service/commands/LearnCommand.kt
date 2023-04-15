package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.SpellEnum
import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.model.TwitchMessage
import com.floxd.hogwartsbot.service.SpellService
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service
import javax.transaction.NotSupportedException

@Service
class LearnCommand(val spellService: SpellService) : Command() {
    override fun commandName(): String {
        return "learn"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("learn", "learn a new spell")
                .addOption(OptionType.STRING, "spell", "Chose the spell to learn. Available spells: Expelliarmus", true)
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        val spellOption = event.getOption("spell") ?: throw BotException("No spell was provided")
        val spell = SpellEnum.values().firstOrNull { it.spellName.lowercase().startsWith(spellOption.asString.lowercase()) }
                ?: return MessageEmbedFactory.create("Learn spell", "This spell is unavailable, or you misspelled its name.")

        val user = event.member
        if (user == null) throw BotException("This shouldn't have happened - Couldn't find the user in this server")
        return MessageEmbedFactory.create("Learn ${spell.spellName}", spellService.learnSpell(user, spell))
    }

    override fun twitchCommand(message: TwitchMessage): String {
        throw NotSupportedException()
    }
}
