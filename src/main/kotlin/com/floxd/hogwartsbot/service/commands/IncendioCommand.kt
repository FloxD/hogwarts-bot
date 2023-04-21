package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.SpellEnum
import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.model.TwitchMessage
import com.floxd.hogwartsbot.service.EffectService
import com.floxd.hogwartsbot.service.SpellService
import com.floxd.hogwartsbot.service.UserService
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service

@Service
class IncendioCommand(effectService: EffectService,
                      spellService: SpellService,
                      userService: UserService) : MagicCommand(effectService, spellService, userService) {
    override fun commandName(): String {
        return "incendio"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("incendio", "A fire spell that makes the bot react to the target's messages with a fire emoji.")
                .addOption(OptionType.USER, "target", "The user whom you want to cast the spell on.", true)
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        val targetMember = getTargetMember(event)
        val caster = event.member
        if (caster == null) {
            throw BotException("This shouldn't have happened - Can't find the caster in this server")
        }

        val message = when (spellService.castSpell(SpellEnum.INCENDIO, caster, targetMember)) {
            SpellService.CastingResult.SUCCESS -> "<@${targetMember.id}> is on fire for ${SpellEnum.INCENDIO.durationInHours} hrs."
            SpellService.CastingResult.ALREADY_AFFECTED -> "<@${targetMember.id}> is already on fire."
            SpellService.CastingResult.COOLDOWN -> "Your Incendio spell is currently on cooldown."
            SpellService.CastingResult.FAILED -> "Nothing happened! You need to learn the spell first, in order for you to use it."
        }
        return MessageEmbedFactory.create("Casted Incendio", message)
    }

    override fun twitchCommand(message: TwitchMessage): String {
        TODO("Not yet implemented")
    }
}
