package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import com.floxd.hogwartsbot.service.EffectService
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


    /**
     * amount of exp it costs to learn the spell
     */
    abstract fun cost(): Long

    override fun runDiscord(event: SlashCommandInteractionEvent): MessageEmbed {
        if (effectService.checkForExpelliarmusEffect(event.member)) {
            return super.runDiscord(event)
        } else {
            return MessageEmbedFactory.create("You can't cast ${this.commandName()}", "Someone casted expelliarmus on you")
        }
    }

    // TODO: handle twitch
    // currently twitch commands are unaffected by expelliarmus
    // but it's no problem rn because twitch doesn't implement any magic commands so far
}
