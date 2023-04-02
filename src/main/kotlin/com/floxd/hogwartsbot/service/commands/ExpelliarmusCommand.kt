package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.model.TwitchMessage
import com.floxd.hogwartsbot.service.EffectService
import com.floxd.hogwartsbot.service.UserService
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service
import javax.transaction.NotSupportedException

@Service
class ExpelliarmusCommand (val userService: UserService, val effectService: EffectService) :Command() {
    override fun commandName(): String {
        return "expelliarmus"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("expelliarmus", "A disarming spell that makes the target unable to cast spells or practice magic.")
            .addOption(OptionType.USER, "target", "The user whom you want to cast the spell on.",true)
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        val targetOption = event.getOption("target")
        return MessageEmbedFactory.create("Expelliarmus", effectService.castExpelliarmus(targetOption, event.member, userService.expAsLong(event.member)))
    }

    override fun twitchCommand(message: TwitchMessage): String {
        throw NotSupportedException()
    }

}
