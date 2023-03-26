package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.entity.Platform
import com.floxd.hogwartsbot.entity.Link
import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.repository.LinkRepository
import com.floxd.hogwartsbot.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

/**
 * this service is for linking twitch and discord account
 * this can be done bidirectionally and the process looks like the following:
 *
 * 1. initiate linking request in discord by using command "/link {twitchName}"
 * 2. complete linking request in twitch chat by using command "?link {discordId}"
 * or the other way round
 *
 * since it could happen that a user has a both twitch and discord account separatly in the db
 * we have to merge the user when the linking process finishes
 * when both exist we make the user with the highest exp the main user and delete the other user
 */
@Service
class LinkService(val linkRepository: LinkRepository,
                  val userRepository: UserRepository) {

    private val LOGGER = LoggerFactory.getLogger(LinkService::class.java)
    private val RANDOM = Random()

    /**
     * this method is called when using twitch command ?link
     */
    fun linkTwitch(twitchName: String, discordId: String): String {
        val link = linkRepository.getLinkByDiscordId(discordId)

        link?.let {
            return link(Platform.TWITCH, discordId, twitchName, it)
        } ?: run {
            val user = userRepository.findByTwitchName(twitchName)
            if (user?.discordId != null) {
                throw BotException("Your Twitch account is already linked to a Discord account with ID ${user.discordId}")
            }

            linkRepository.save(Link(RANDOM.nextLong(), Platform.TWITCH, discordId, twitchName, LocalDateTime.now()))
            return "Saved linking request in database. You can now use the /link discord command to finish linking"
        }
    }


    /**
     * this method is called when using discord command /link
     */
    fun linkDiscord(discordId: String, twitchName: String): String {
        val link = linkRepository.getLinkByTwitchName(twitchName)

        link?.let {
            return link(Platform.DISCORD, discordId, twitchName, it)
        } ?: run {
            val user = userRepository.findByDiscordId(discordId)
            if (user?.twitchName != null) {
                throw BotException("Your Discord account is already linked to a Twitch account with username ${user.twitchName}")
            }

            linkRepository.save(Link(RANDOM.nextLong(), Platform.DISCORD, discordId, twitchName, LocalDateTime.now()))
            return "Saved linking request in database. You can now use the ?link twitch command to finish linking"
        }
    }

    private fun link(platform: Platform,
                     discordId: String,
                     twitchName: String,
                     link: Link): String {
        if (platform == link.initiator) {
            throw BotException("You started the linking process through ${link.initiator.platformName}. " +
                    "You have to finish the process through ${if (link.initiator == Platform.DISCORD) "Twitch" else "Discord"}.")
        }

        LOGGER.info("found link in db (${discordId} ${twitchName}) will attempt to finish linking request")

        if (link.twitchName == twitchName && link.discordId == discordId) {
            val discordUser = userRepository.findByDiscordId(discordId)
            val twitchUser = userRepository.findByTwitchName(twitchName)

            if (discordUser == null && twitchUser == null) {
                throw BotException("There's no user in db with either twitch name ${twitchName} nor discord id ${discordId}")
            }

            // discord user isn't in db but twitch user is
            if (discordUser == null && twitchUser != null) {
                LOGGER.info("Found twitch user but no discord user. Adding discord id to twitch user.")
                twitchUser.discordId = discordId
                userRepository.save(twitchUser)
            }

            // discord user is in db but twitch user isn't
            if (discordUser != null && twitchUser == null) {
                LOGGER.info("Found discord user but no twitch user. Adding twitch name to discord user.")
                discordUser.twitchName = twitchName
                userRepository.save(discordUser)
            }

            // both exist
            if (discordUser != null && twitchUser != null) {
                LOGGER.info("Found both discord user and twitch user.")
                // make the user with more exp the main user and delete the other user
                if (discordUser.exp > twitchUser.exp) {
                    LOGGER.info("Discord user will become main user.")
                    discordUser.twitchName = twitchName
                    userRepository.save(discordUser)
                    userRepository.delete(twitchUser)
                } else {
                    LOGGER.info("Twitch user will become main user.")
                    twitchUser.discordId = discordId
                    userRepository.save(twitchUser)
                    userRepository.delete(discordUser)
                }
            }

            linkRepository.delete(link)
            return "Linking Success"
        } else {
            throw BotException("The Discord ID or Twitch username in database don't match")
        }
    }
}
