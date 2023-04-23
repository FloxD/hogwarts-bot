package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.SpellEnum
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DiscordMessageListener(val effectService: EffectService,
                             @Value("\${incendio.enabled}") private val incendioEnabled: Boolean) : ListenerAdapter() {

    private val LOGGER = LoggerFactory.getLogger(DiscordMessageListener::class.java)

    override fun onMessageReceived(event: MessageReceivedEvent) {
        try {
            if (effectService.checkForEffect(SpellEnum.INCENDIO, event.author.id).isAffected && incendioEnabled) {
                //React to the message with a fire emoji.
                event.message.addReaction(Emoji.fromUnicode("U+1F525")).queue()
            }
        } catch (e: Exception) {
            LOGGER.error("Unknown error happened.", e)
        }
    }
}
