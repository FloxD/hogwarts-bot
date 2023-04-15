package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.SpellEnum
import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.model.TwitchMessage
import com.floxd.hogwartsbot.service.EffectService
import com.floxd.hogwartsbot.service.SpellService
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service
import javax.transaction.NotSupportedException

@Service
class ExpelliarmusCommand(effectService: EffectService, val spellService: SpellService) : MagicCommand(effectService) {
    override fun commandName(): String {
        return "expelliarmus"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("expelliarmus", "A disarming spell that makes the target unable to cast spells or practice magic.")
                .addOption(OptionType.USER, "target", "The user whom you want to cast the spell on.", true)
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        val targetMember = getTargetMember(event)

        val caster = event.member
        if (caster == null) {
            throw BotException("This shouldn't have happened - Can't find the caster in this server")
        }

        val message = when (spellService.castSpell(SpellEnum.EXPELLIARMUS, caster, targetMember)) {
            SpellService.CastingResult.SUCCESS -> "<@${targetMember.id}>'s wand flew off. They'll get it back in ${SpellEnum.EXPELLIARMUS.durationInHours} hrs."
            SpellService.CastingResult.ALREADY_AFFECTED -> "<@${targetMember.id}> is already disarmed and can't use magic for now."
            SpellService.CastingResult.COOLDOWN -> "Your Explelliarmus spell is currently on cooldown."
            SpellService.CastingResult.FAILED -> "Nothing happened! You need to learn the spell first, in order for you to use it."
        }

        return MessageEmbedFactory.create("Casted Expelliarmus", message)
    }

    override fun twitchCommand(message: TwitchMessage): String {
        throw NotSupportedException()
    }

}
