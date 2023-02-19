package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.exception.BotException
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DiscordCommandListener(val commands: List<Command>) : ListenerAdapter() {

    private val LOGGER = LoggerFactory.getLogger(DiscordCommandListener::class.java)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        try {
            commands.find { it.commandName() == event.name }?.let {
                val message = it.runDiscord(event)
                event.replyEmbeds(message).queue()
            } ?: run {
                event.reply("Command ${event.name} not found").setEphemeral(true).queue()
            }
        } catch (e: BotException) {
            event.reply(e.message).setEphemeral(true).queue()
        } catch (e: UnsupportedOperationException) {
            event.reply("Not implemented yet").setEphemeral(true).queue()
        } catch (e: Exception) {
            event.reply("Unknown error happened. Message FloxD if you need help.").setEphemeral(true).queue()
            LOGGER.error("Unknown error happened.", e)
        }
    }
}
