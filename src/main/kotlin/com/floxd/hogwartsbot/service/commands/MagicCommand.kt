package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.SpellEnum
import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.service.EffectService
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

/**
 * we call "magic commands" all commands that need magic to be executed
 * e.g. normal spells like Expelliarmus counts as a magic command but also the Practice Magic command counts as
 * a magic command
 *
 * commands like Points, Link, Leaderboard are non-magical commands
 */
abstract class MagicCommand(val effectService: EffectService) : Command() {


    override fun runDiscord(event: SlashCommandInteractionEvent): MessageEmbed {
        val caster = event.member
        if (caster == null) throw BotException("No user selected")

        val effectResult = effectService.checkForEffect(SpellEnum.EXPELLIARMUS, caster.id)
        if (effectResult.isAffected) {
            return MessageEmbedFactory.create(
                    "You can't cast ${this.commandName()}",
                    "Someone has casted *Expelliarmus* on you!" +
                            " You'll get your wand back in" +
                            " ${effectResult.timeLeft.toHoursPart()} hrs ${effectResult.timeLeft.toMinutesPart()} mins"
            )
        } else {
            return super.runDiscord(event)
        }
    }

    fun getTargetMember(event: SlashCommandInteractionEvent): Member {
        val option = event.getOption("target") ?: throw BotException("You need to specify a target")
        return option.asMember ?: throw BotException("User is not on this server")
    }

    // TODO: handle twitch
    // currently twitch commands are unaffected by expelliarmus
    // but it's no problem rn because twitch doesn't implement any magic commands so far
}
