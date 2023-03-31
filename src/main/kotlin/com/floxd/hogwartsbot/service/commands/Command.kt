package com.floxd.hogwartsbot.service.commands

import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.model.TwitchMessage
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

abstract class Command {

    abstract fun commandName(): String
    abstract fun needsModPermissions(): Boolean
    abstract fun slashCommandData(): SlashCommandData
    abstract fun discordCommand(event: SlashCommandInteractionEvent): MessageEmbed
    abstract fun twitchCommand(message: TwitchMessage): String


    private val USER_ID_FLOXD = "132602254531362817"
    private val MOD_GROUP_ID = "481726205603741696"
    private val MOD_USER_IDS = setOf(USER_ID_FLOXD)
    private val MOD_GROUP_IDS = setOf(MOD_GROUP_ID)

    private val TWITCH_MODS = listOf(
            "floxd",
            "elina",
            "360zeus",
            "epicdonutdude_",
            "jeffjeffingson",
            "koksalot",
            "kromis",
            "smartbutautistic",
            "teemtron",
            "thedangerousbros",
            "tolekk",
            "trouserdemon",
            "unfortunatelyaj",
            "zugren"
    )

    open fun runDiscord(event: SlashCommandInteractionEvent): MessageEmbed {
        if (needsModPermissions()) {
            if (hasDiscordModPrivileges(event.member)) {
                return discordCommand(event)
            } else {
                throw BotException("You need mod permissions to execute this command.")
            }
        } else {
            return discordCommand(event)
        }
    }

    open fun runTwitch(message: TwitchMessage): String {
        if (needsModPermissions()) {
            if (hasTwitchModPrivileges(message.username)) {
                return twitchCommand(message)
            } else {
                throw BotException("You need mod permissions to execute this command.")
            }
        } else {
            return twitchCommand(message)
        }
    }

    private fun hasDiscordModPrivileges(user: Member?): Boolean {
        val id = user?.user?.id ?: return false

        if (MOD_USER_IDS.contains(id)) {
            return true
        }

        val groupIds = user.roles

        for (groupId in groupIds) {
            if (MOD_GROUP_IDS.contains(groupId.id)) {
                return true
            }
        }

        return false
    }

    private fun hasTwitchModPrivileges(username: String): Boolean {
        return TWITCH_MODS.contains(username)
    }
}
