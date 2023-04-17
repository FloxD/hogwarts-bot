package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.service.commands.Command
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component


@Component
class DiscordBotRunner(val discordCommandListener: DiscordCommandListener,
                       val discordMessageListener: DiscordMessageListener,
                       val commands: List<Command>) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val token = System.getenv("DISCORD_TOKEN")
        assert(token != null, { "env var DISCORD_TOKEN must be set" })
        val builder: JDABuilder = JDABuilder.createDefault(token)

        // Set activity (like "playing Something")
        builder.setActivity(Activity.watching("Harry Potter"))

        val jda = builder.build()

        // These commands might take a few minutes to be active after creation/update/delete
        val commands = jda.updateCommands()

        commands.addCommands(this.commands.map { it.slashCommandData() })
        commands.queue()

        jda.addEventListener(discordCommandListener)
        jda.addEventListener(discordMessageListener)
    }
}
