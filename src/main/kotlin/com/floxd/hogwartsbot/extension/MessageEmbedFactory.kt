package com.floxd.hogwartsbot.extension

import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color

class MessageEmbedFactory {

    companion object {
        fun create(title: String, description: String): MessageEmbed {
            return MessageEmbed(
                null,
                title,
                description,
                EmbedType.RICH,
                null,
                Color(26, 188, 156).rgb,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            )
        }
    }
}
