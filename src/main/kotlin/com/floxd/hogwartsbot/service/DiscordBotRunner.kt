package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.HouseEnum
import com.floxd.hogwartsbot.entity.House
import com.floxd.hogwartsbot.repository.HouseRepository
import com.floxd.hogwartsbot.service.commands.Command
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component


@Component
class DiscordBotRunner(val houseRepository: HouseRepository,
                       val discordCommandListener: DiscordCommandListener,
                       val commands: List<Command>) : CommandLineRunner {

    override fun run(vararg args: String?) {

        val gryffindor = houseRepository.findById(HouseEnum.GRYFFINDOR.houseId)
        val hufflepuff = houseRepository.findById(HouseEnum.HUFFLEPUFF.houseId)
        val ravenclaw = houseRepository.findById(HouseEnum.RAVENCLAW.houseId)
        val slytherin = houseRepository.findById(HouseEnum.SLYTHERIN.houseId)

        if (gryffindor.isEmpty) {
            houseRepository.save(House(HouseEnum.GRYFFINDOR.houseId, HouseEnum.GRYFFINDOR.houseName, 0L))
        }
        if (hufflepuff.isEmpty) {
            houseRepository.save(House(HouseEnum.HUFFLEPUFF.houseId, HouseEnum.HUFFLEPUFF.houseName, 0L))
        }
        if (ravenclaw.isEmpty) {
            houseRepository.save(House(HouseEnum.RAVENCLAW.houseId, HouseEnum.RAVENCLAW.houseName, 0L))
        }
        if (slytherin.isEmpty) {
            houseRepository.save(House(HouseEnum.SLYTHERIN.houseId, HouseEnum.SLYTHERIN.houseName, 0L))
        }

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
    }
}
