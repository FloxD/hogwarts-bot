package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.model.TwitchMessage
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.springframework.stereotype.Service

@Service
class HelpCommand : Command() {
    override fun commandName(): String {
        return "help"
    }

    override fun needsModPermissions(): Boolean {
        return false
    }

    override fun slashCommandData(): SlashCommandData {
        return Commands.slash("help", "not implemented")
    }

    override fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed {
        throw UnsupportedOperationException()
    }

    override fun twitchCommand(message: TwitchMessage): String {
        val command = message.message.split(" ")
        if (command.size == 1) {
            val s = "Available commands are ?ping, ?points, ?addpoints, ?subtractpoints"
            return "Use ?help [command] to get more info. $s"
        } else if (command.size > 1) {
            when (command[1]) {
                "?ping", "ping" -> {
                    return "This command is for checking if the Bot is running."

                }

                "?points", "points" -> {
                    return "Use ?points or ?points [house] to see how many points each house has."

                }

                "?addpoints", "addpoints" -> {
                    return "Use ?addpoints [house] [points] to add points to a house. Mod only command"

                }

                "?subtractpoints", "subtractpoints" -> {
                    return "Use ?subtractpoints [house] [points] to subtract points from a house. Mod only command"

                }

                else -> return "the command \"${command[1]}\" does not exist"
            }
        } else {
            return "penis"
        }
    }
}
