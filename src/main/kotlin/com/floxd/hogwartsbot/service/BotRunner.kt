package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.entity.House
import com.floxd.hogwartsbot.repository.HouseRepository
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component


@Component
class BotRunner(val houseRepository: HouseRepository,
                val commandListener: CommandListener) : CommandLineRunner {

    override fun run(vararg args: String?) {

        val gryffindor = houseRepository.findById(1)
        val hufflepuff = houseRepository.findById(2)
        val ravenclaw = houseRepository.findById(3)
        val slytherin = houseRepository.findById(4)

        if (gryffindor.isEmpty) {
            houseRepository.save(House(1, "Gryffindor", 0))
        }
        if (hufflepuff.isEmpty) {
            houseRepository.save(House(2, "Hufflepuff", 0))
        }
        if (ravenclaw.isEmpty) {
            houseRepository.save(House(3, "Ravenclaw", 0))
        }
        if (slytherin.isEmpty) {
            houseRepository.save(House(4, "Slytherin", 0))
        }

        val token = System.getenv("DISCORD_TOKEN")
        assert(token != null, { "env var DISCORD_TOKEN must be set" })
        val builder: JDABuilder = JDABuilder.createDefault(token)

        // Set activity (like "playing Something")
        builder.setActivity(Activity.watching("Harry Potter"))

        val jda = builder.build()

        // These commands might take a few minutes to be active after creation/update/delete
        val commands = jda.updateCommands()

        commands.addCommands(
            Commands.slash("points", "view points from house")
                .addOption(OptionType.STRING, "house", "Gryffindor, Hufflepuff, Ravenclaw or Slytherin"),
            Commands.slash("addpoints", "add points to house")
                .addOption(OptionType.STRING, "house", "Gryffindor, Hufflepuff, Ravenclaw or Slytherin")
                .addOption(OptionType.INTEGER, "points", "how many points you want to add to the house")
                .addOption(OptionType.STRING, "message", "an optional message about why points were added (tip: start the message with 'for ...')"),
            Commands.slash("ping", "check ping")
        )

        commands.queue()

        jda.addEventListener(commandListener)
    }
}
