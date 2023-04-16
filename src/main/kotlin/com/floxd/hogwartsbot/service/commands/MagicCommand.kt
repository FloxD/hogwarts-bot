package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.SpellEnum
import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.service.EffectService
import com.floxd.hogwartsbot.service.SpellService
import com.floxd.hogwartsbot.service.UserService
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.time.LocalDateTime

/**
 * we call "magic commands" all commands that need magic to be executed
 * e.g. normal spells like Expelliarmus counts as a magic command but also the Practice Magic command counts as
 * a magic command
 *
 * commands like Points, Link, Leaderboard are non-magical commands
 */
abstract class MagicCommand(val effectService: EffectService,
                            val spellService: SpellService,
                            val userService: UserService) : Command() {


    override fun runDiscord(event: SlashCommandInteractionEvent): MessageEmbed {
        val caster = event.member
        if (caster == null) throw BotException("No user selected")

        // check caster for expelliarmus
        var effectResult = effectService.checkForEffect(SpellEnum.EXPELLIARMUS, caster.id)
        if (effectResult.isAffected) {
            return MessageEmbedFactory.create(
                    "You can't cast ${this.commandName()}",
                    "Someone has casted *Expelliarmus* on you!" +
                            " You'll get your wand back in" +
                            " ${effectResult.timeLeft.toHoursPart()} hrs ${effectResult.timeLeft.toMinutesPart()} mins"
            )
        }

        // check target for protego
        val targetOption = event.getOption("target")
        if (targetOption != null) {
            effectResult = effectService.checkForEffect(SpellEnum.PROTEGO, targetOption.asUser.id)
            val effectRecord = effectResult.effectRecord
            if (effectResult.isAffected && effectRecord != null) {

                // the spell the caster casted is getting a cooldown
                val spell = SpellEnum.valueOf(this.commandName().uppercase())
                val casterUser = userService.getUser(caster.id) ?: userService.addUser(caster)
                spellService.updateLastCast(spell, casterUser.id, LocalDateTime.now())

                // the "last applied date" of the targets protego is set to 1 year in the past
                // which means the protego spell only protects you from one spell
                effectRecord.lastApplied = effectRecord.lastApplied.minusYears(1)
                effectService.saveEffect(effectRecord)

                return MessageEmbedFactory.create(
                        "Casted ${spell.spellName}",
                        "Your spell was blocked by Protego!"
                )
            }
        }

        return super.runDiscord(event)
    }

    fun getTargetMember(event: SlashCommandInteractionEvent): Member {
        val option = event.getOption("target") ?: throw BotException("You need to specify a target")
        return option.asMember ?: throw BotException("User is not on this server")
    }

    // TODO: handle twitch
    // currently twitch commands are unaffected by expelliarmus
    // but it's no problem rn because twitch doesn't implement any magic commands so far
}
