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
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service

@Service
class ProtegoCommand(effectService: EffectService,
                     spellService: SpellService,
                     userService: UserService) : MagicCommand(effectService, spellService, userService) {
    override fun commandName(): String {
        return "protego"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("protego", "Protect yourself with a magical shield that blocks one spell.")
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        val caster = event.member
        if (caster == null) {
            throw BotException("This shouldn't have happened - Can't find the caster in this server")
        }

        val message = when (spellService.castSpell(SpellEnum.PROTEGO, caster, caster)) {
            SpellService.CastingResult.SUCCESS -> "You put a magical shield around you. If someone casts a spell on you, its effect will be negated but your shield will go down."
            SpellService.CastingResult.ALREADY_AFFECTED -> "You are already under the effect of this spell."
            SpellService.CastingResult.COOLDOWN -> "Your Protego spell is currently on cooldown."
            SpellService.CastingResult.FAILED -> "Nothing happened! You need to learn the spell first, in order for you to use it."
        }
        return MessageEmbedFactory.create("Casted Protego", message)
    }

    override fun twitchCommand(message: TwitchMessage): String {
        TODO("Not yet implemented")
    }

}
