package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.extension.MessageEmbedFactory
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DiscordCommandListener(val houseService: HouseService,
                             val userService: UserService) : ListenerAdapter() {

    private val LOGGER = LoggerFactory.getLogger(DiscordCommandListener::class.java)

    private val USER_ID_FLOXD = "132602254531362817"
    private val MOD_GROUP_ID = "481726205603741696"
    private val MOD_USER_IDS = setOf(USER_ID_FLOXD)
    private val MOD_GROUP_IDS = setOf(MOD_GROUP_ID)

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        try {
            when (event.name) {
                "points" -> {
                    val houseOption = event.getOption("house")

                    houseOption?.let {
                        event.reply(houseService.getPoints(it.asString)).queue()
                    } ?: run {
                        event.reply(houseService.getAllPoints()).queue()
                    }
                }

                "addpoints" -> {
                    if (hasModPrivileges(event.member)) {
                        val userOption = event.getOption("user")
                        val houseOption = event.getOption("house")
                        val pointsToAddOption = event.getOption("points") ?: throw BotException("Points must be set")
                        val messageOption = event.getOption("message")

                        if (userOption != null) {
                            val message =
                                houseService.addPointsUser(event.member, userOption, pointsToAddOption, messageOption)
                            event.reply(message).queue()
                        } else if (houseOption != null) {
                            val message =
                                houseService.addPointsHouse(event.member, houseOption, pointsToAddOption, messageOption)
                            event.reply(message).queue()
                        } else {
                            throw BotException("Either a house or user must be selected")
                        }
                    } else {
                        event.reply("You need to have mod permissions to execute this command")
                            .setEphemeral(true)
                            .queue()
                    }
                }

                "subtractpoints" -> {
                    if (hasModPrivileges(event.member)) {
                        val userOption = event.getOption("user")
                        val houseOption = event.getOption("house")
                        val pointsToAddOption = event.getOption("points") ?: throw BotException("Points must be set")
                        val messageOption = event.getOption("message")

                        if (userOption != null) {
                            val message = houseService.subtractPointsUser(
                                event.member,
                                userOption,
                                pointsToAddOption,
                                messageOption
                            )
                            event.reply(message).queue()
                        } else if (houseOption != null) {
                            val message = houseService.subtractPointsHouse(
                                event.member,
                                houseOption,
                                pointsToAddOption,
                                messageOption
                            )
                            event.reply(message).queue()
                        } else {
                            throw BotException("Either a house or user must be selected")
                        }
                    } else {
                        event.reply("You need to have mod permissions to execute this command")
                            .setEphemeral(true)
                            .queue()
                    }
                }

                "practicemagic" -> {
                    val message = userService.practiceMagic(event.member)
                    event.reply(message).queue()
                }

                "exp" -> {
                    val userOption = event.getOption("user")

                    if (userOption != null) {
                        val message = userService.exp(userOption)
                        event.reply(message).queue()
                    } else {
                        val message = userService.exp(event.member)
                        event.reply(message).queue()
                    }
                }

                "leaderboard" -> {
                    val message = userService.leaderBoard()
                    event.replyEmbeds(MessageEmbedFactory.create("Exp Leaderboard", message)).queue()
                }

                "ping" -> {
                    val time = System.currentTimeMillis()
                    event.reply("Pong!")
                        .setEphemeral(true)
                        .flatMap { v ->
                            event.hook.editOriginalFormat(
                                "Pong: %d ms",
                                System.currentTimeMillis() - time
                            )
                        }
                        .queue()
                }
            }
        } catch (e: BotException) {
            event.reply(e.message).setEphemeral(true).queue()
        } catch (e: Exception) {
            event.reply("Unknown error happened. Message FloxD if you need help.").setEphemeral(true).queue()
            LOGGER.error("Unknown error happened.", e)
        }
    }

    private fun hasModPrivileges(user: Member?): Boolean {
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
}
