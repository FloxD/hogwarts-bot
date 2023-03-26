package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.entity.Platform
import com.floxd.hogwartsbot.entity.Link
import com.floxd.hogwartsbot.entity.User
import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.repository.LinkRepository
import com.floxd.hogwartsbot.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime


private const val DISCORD_ID_CORRECT = "DiscordID123"
private const val TWITCH_NAME_CORRECT = "TwitchName123"

private const val DISCORD_ID_INCORRECT = "DiscordID987"
private const val TWITCH_NAME_INCORRECT = "TwitchName987"

@ExtendWith(MockitoExtension::class)
class LinkServiceTest {

    @Mock
    lateinit var linkRepository: LinkRepository

    @Mock
    lateinit var userRepository: UserRepository

    lateinit var linkService: LinkService

    @BeforeEach
    fun setUp() {
        linkService = LinkService(linkRepository, userRepository)
    }

    /**
     * Twitch Linking
     */

    @Test
    fun `happy path, twitch link, execute linking command through twitch, no linking request exists`() {
        `when`(linkRepository.getLinkByDiscordId(DISCORD_ID_CORRECT)).thenReturn(null)

        val message = linkService.linkTwitch(TWITCH_NAME_CORRECT, DISCORD_ID_CORRECT)

        val captor = ArgumentCaptor.forClass(Link::class.java)
        verify(linkRepository).save(captor.capture())
        assertEquals(Platform.TWITCH, captor.value.initiator)
        assertEquals(DISCORD_ID_CORRECT, captor.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captor.value.twitchName)
        assertTrue(captor.value.creationDate.isBefore(LocalDateTime.now().plusMinutes(5)))
        assertEquals(
            "Saved linking request in database. You can now use the /link discord command to finish linking",
            message
        )
    }

    @Test
    fun `happy path, twitch link, execute linking command through twitch, discord started linking request, only discord user exists`() {
        `when`(linkRepository.getLinkByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(Link(111, Platform.DISCORD, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        `when`(userRepository.findByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(User(222, DISCORD_ID_CORRECT, null, 777, LocalDateTime.now()))

        `when`(userRepository.findByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(null)

        val message = linkService.linkTwitch(TWITCH_NAME_CORRECT, DISCORD_ID_CORRECT)

        val captorUser = ArgumentCaptor.forClass(User::class.java)
        verify(userRepository).save(captorUser.capture())
        assertEquals(222, captorUser.value.id)
        assertEquals(DISCORD_ID_CORRECT, captorUser.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorUser.value.twitchName)
        assertEquals(777, captorUser.value.exp)

        val captorLink = ArgumentCaptor.forClass(Link::class.java)
        verify(linkRepository).delete(captorLink.capture())
        assertEquals(DISCORD_ID_CORRECT, captorLink.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorLink.value.twitchName)
        assertTrue(captorLink.value.creationDate.isBefore(LocalDateTime.now()))
        assertEquals("Linking Success", message)
    }

    @Test
    fun `happy path, twitch link, execute linking command through twitch, discord started linking request, only twitch user exists`() {
        `when`(linkRepository.getLinkByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(Link(111, Platform.DISCORD, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        `when`(userRepository.findByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(null)

        `when`(userRepository.findByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(User(333, null, TWITCH_NAME_CORRECT, 888, LocalDateTime.now()))

        val message = linkService.linkTwitch(TWITCH_NAME_CORRECT, DISCORD_ID_CORRECT)

        val captorUser = ArgumentCaptor.forClass(User::class.java)
        verify(userRepository).save(captorUser.capture())
        assertEquals(333, captorUser.value.id)
        assertEquals(DISCORD_ID_CORRECT, captorUser.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorUser.value.twitchName)
        assertEquals(888, captorUser.value.exp)

        val captorLink = ArgumentCaptor.forClass(Link::class.java)
        verify(linkRepository).delete(captorLink.capture())
        assertEquals(DISCORD_ID_CORRECT, captorLink.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorLink.value.twitchName)
        assertTrue(captorLink.value.creationDate.isBefore(LocalDateTime.now()))
        assertEquals("Linking Success", message)
    }

    @Test
    fun `happy path, twitch link, execute linking command through twitch, discord started linking request, both users exists, user with higher exp becomes main user`() {
        `when`(linkRepository.getLinkByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(Link(111, Platform.DISCORD, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        `when`(userRepository.findByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(User(222, DISCORD_ID_CORRECT, null, 777, LocalDateTime.now()))

        `when`(userRepository.findByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(User(333, null, TWITCH_NAME_CORRECT, 888, LocalDateTime.now()))

        val message = linkService.linkTwitch(TWITCH_NAME_CORRECT, DISCORD_ID_CORRECT)

        val captorUser = ArgumentCaptor.forClass(User::class.java)
        verify(userRepository).save(captorUser.capture())
        assertEquals(333, captorUser.value.id)
        assertEquals(DISCORD_ID_CORRECT, captorUser.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorUser.value.twitchName)
        assertEquals(888, captorUser.value.exp)

        val captorLink = ArgumentCaptor.forClass(Link::class.java)
        verify(linkRepository).delete(captorLink.capture())
        assertEquals(DISCORD_ID_CORRECT, captorLink.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorLink.value.twitchName)
        assertTrue(captorLink.value.creationDate.isBefore(LocalDateTime.now()))
        assertEquals("Linking Success", message)
    }

    @Test
    fun `unhappy path, twitch link, execute linking command through twitch, account already linked`() {
        `when`(linkRepository.getLinkByDiscordId(DISCORD_ID_CORRECT)).thenReturn(null)
        `when`(userRepository.findByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(User(333, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, 888, LocalDateTime.now()))

        assertThrows(
            BotException::class.java,
            { linkService.linkTwitch(TWITCH_NAME_CORRECT, DISCORD_ID_CORRECT) },
            "Your Twitch account is already linked to a Discord account with ID ${DISCORD_ID_CORRECT}"
        )
    }

    @Test
    fun `unhappy path, twitch link, execute linking command through twitch, no users exist`() {
        `when`(linkRepository.getLinkByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(Link(111, Platform.DISCORD, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        `when`(userRepository.findByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(null)

        `when`(userRepository.findByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(null)

        assertThrows(
            BotException::class.java,
            { linkService.linkTwitch(TWITCH_NAME_CORRECT, DISCORD_ID_CORRECT) },
            "There's no user in db with either twitch name ${TWITCH_NAME_CORRECT} nor discord id ${DISCORD_ID_CORRECT}"
        )
    }

    @Test
    fun `unhappy path, twitch link, twitch name doesn't match`() {
        `when`(linkRepository.getLinkByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(Link(111, Platform.DISCORD, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        assertThrows(
            BotException::class.java,
            { linkService.linkTwitch(TWITCH_NAME_INCORRECT, DISCORD_ID_CORRECT) },
            "The Discord ID or Twitch username in database don't match"
        )
    }

    @Test
    fun `unhappy path, twitch link, discord id doesn't match`() {
        `when`(linkRepository.getLinkByDiscordId(DISCORD_ID_INCORRECT))
            .thenReturn(Link(111, Platform.DISCORD, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        assertThrows(
            BotException::class.java,
            { linkService.linkTwitch(TWITCH_NAME_CORRECT, DISCORD_ID_INCORRECT) },
            "The Discord ID or Twitch username in database don't match"
        )
    }

    @Test
    fun `unhappy path, twitch link, discord id and twitch id don't match`() {
        `when`(linkRepository.getLinkByDiscordId(DISCORD_ID_INCORRECT))
            .thenReturn(Link(111, Platform.DISCORD, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        assertThrows(
            BotException::class.java,
            { linkService.linkTwitch(TWITCH_NAME_INCORRECT, DISCORD_ID_INCORRECT) },
            "The Discord ID or Twitch username in database don't match"
        )
    }

    @Test
    fun `unhappy path, twitch link, tried to initate linking from twitch twice`() {
        `when`(linkRepository.getLinkByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(Link(111, Platform.TWITCH, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        assertThrows(
            BotException::class.java,
            { linkService.linkTwitch(TWITCH_NAME_CORRECT, DISCORD_ID_CORRECT) },
            "You started the linking process through Twitch. You have to finish the process through Discord."
        )
    }

    /**
     * Discord Linking
     */
    @Test
    fun `happy path, discord link, execute linking command through discord, no linking request exists`() {
        `when`(linkRepository.getLinkByTwitchName(TWITCH_NAME_CORRECT)).thenReturn(null)

        val message = linkService.linkDiscord(DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT)

        val captor = ArgumentCaptor.forClass(Link::class.java)
        verify(linkRepository).save(captor.capture())
        assertEquals(DISCORD_ID_CORRECT, captor.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captor.value.twitchName)
        assertTrue(captor.value.creationDate.isBefore(LocalDateTime.now().plusMinutes(5)))
        assertEquals(
            "Saved linking request in database. You can now use the ?link twitch command to finish linking",
            message
        )
    }

    @Test
    fun `happy path, discord link, execute linking command through discord, twitch started linking request, only discord user exists`() {
        `when`(linkRepository.getLinkByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(Link(111, Platform.TWITCH, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        `when`(userRepository.findByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(User(222, DISCORD_ID_CORRECT, null, 777, LocalDateTime.now()))

        `when`(userRepository.findByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(null)

        val message = linkService.linkDiscord(DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT)

        val captorUser = ArgumentCaptor.forClass(User::class.java)
        verify(userRepository).save(captorUser.capture())
        assertEquals(222, captorUser.value.id)
        assertEquals(DISCORD_ID_CORRECT, captorUser.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorUser.value.twitchName)
        assertEquals(777, captorUser.value.exp)

        val captorLink = ArgumentCaptor.forClass(Link::class.java)
        verify(linkRepository).delete(captorLink.capture())
        assertEquals(DISCORD_ID_CORRECT, captorLink.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorLink.value.twitchName)
        assertTrue(captorLink.value.creationDate.isBefore(LocalDateTime.now()))
        assertEquals("Linking Success", message)
    }

    @Test
    fun `happy path, discord link, execute linking command through discord, discord started linking request, only twitch user exists`() {
        `when`(linkRepository.getLinkByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(Link(111, Platform.TWITCH, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        `when`(userRepository.findByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(null)

        `when`(userRepository.findByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(User(333, null, TWITCH_NAME_CORRECT, 888, LocalDateTime.now()))

        val message = linkService.linkDiscord(DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT)

        val captorUser = ArgumentCaptor.forClass(User::class.java)
        verify(userRepository).save(captorUser.capture())
        assertEquals(333, captorUser.value.id)
        assertEquals(DISCORD_ID_CORRECT, captorUser.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorUser.value.twitchName)
        assertEquals(888, captorUser.value.exp)

        val captorLink = ArgumentCaptor.forClass(Link::class.java)
        verify(linkRepository).delete(captorLink.capture())
        assertEquals(DISCORD_ID_CORRECT, captorLink.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorLink.value.twitchName)
        assertTrue(captorLink.value.creationDate.isBefore(LocalDateTime.now()))
        assertEquals("Linking Success", message)
    }

    @Test
    fun `happy path, discord link, execute linking command through discord, discord started linking request, both users exists, user with higher exp becomes main user`() {
        `when`(linkRepository.getLinkByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(Link(111, Platform.TWITCH, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        `when`(userRepository.findByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(User(222, DISCORD_ID_CORRECT, null, 777, LocalDateTime.now()))

        `when`(userRepository.findByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(User(333, null, TWITCH_NAME_CORRECT, 888, LocalDateTime.now()))

        val message = linkService.linkDiscord(DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT)

        val captorUser = ArgumentCaptor.forClass(User::class.java)
        verify(userRepository).save(captorUser.capture())
        assertEquals(333, captorUser.value.id)
        assertEquals(DISCORD_ID_CORRECT, captorUser.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorUser.value.twitchName)
        assertEquals(888, captorUser.value.exp)

        val captorLink = ArgumentCaptor.forClass(Link::class.java)
        verify(linkRepository).delete(captorLink.capture())
        assertEquals(DISCORD_ID_CORRECT, captorLink.value.discordId)
        assertEquals(TWITCH_NAME_CORRECT, captorLink.value.twitchName)
        assertTrue(captorLink.value.creationDate.isBefore(LocalDateTime.now()))
        assertEquals("Linking Success", message)
    }

    @Test
    fun `unhappy path, discord link, execute linking command through discord, account already linked`() {
        `when`(linkRepository.getLinkByTwitchName(TWITCH_NAME_CORRECT)).thenReturn(null)
        `when`(userRepository.findByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(User(333, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, 888, LocalDateTime.now()))

        assertThrows(
            BotException::class.java,
            { linkService.linkDiscord(DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT) },
            "Your Discord account is already linked to a Twitch account with username ${TWITCH_NAME_CORRECT}"
        )
    }

    @Test
    fun `unhappy path, discord link, execute linking command through discord, no users exist`() {
        `when`(linkRepository.getLinkByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(Link(111, Platform.TWITCH, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        `when`(userRepository.findByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(null)

        `when`(userRepository.findByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(null)

        assertThrows(
            BotException::class.java,
            { linkService.linkDiscord(DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT) },
            "There's no user in db with either twitch name ${TWITCH_NAME_CORRECT} nor discord id ${DISCORD_ID_CORRECT}"
        )
    }

    @Test
    fun `unhappy path, discord link, twitch name doesn't match`() {
        `when`(linkRepository.getLinkByTwitchName(TWITCH_NAME_INCORRECT))
            .thenReturn(Link(111, Platform.TWITCH, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        assertThrows(
            BotException::class.java,
            { linkService.linkDiscord(DISCORD_ID_CORRECT, TWITCH_NAME_INCORRECT) },
            "The Discord ID or Twitch username in database don't match"
        )
    }

    @Test
    fun `unhappy path, discord link, discord id doesn't match`() {
        `when`(linkRepository.getLinkByTwitchName(TWITCH_NAME_CORRECT))
            .thenReturn(Link(111, Platform.TWITCH, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        assertThrows(
            BotException::class.java,
            { linkService.linkDiscord(DISCORD_ID_INCORRECT, TWITCH_NAME_CORRECT) },
            "The Discord ID or Twitch username in database don't match"
        )
    }

    @Test
    fun `unhappy path, discord link, discord id and twitch id don't match`() {
        `when`(linkRepository.getLinkByTwitchName(TWITCH_NAME_INCORRECT))
            .thenReturn(Link(111, Platform.TWITCH, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        assertThrows(
            BotException::class.java,
            { linkService.linkDiscord(DISCORD_ID_INCORRECT, TWITCH_NAME_INCORRECT) },
            "The Discord ID or Twitch username in database don't match"
        )
    }

    @Test
    fun `unhappy path, discord link, tried to initate linking from discord twice`() {
        `when`(linkRepository.getLinkByDiscordId(DISCORD_ID_CORRECT))
            .thenReturn(Link(111, Platform.DISCORD, DISCORD_ID_CORRECT, TWITCH_NAME_CORRECT, LocalDateTime.now()))

        assertThrows(
            BotException::class.java,
            { linkService.linkTwitch(TWITCH_NAME_CORRECT, DISCORD_ID_CORRECT) },
            "You started the linking process through Discord. You have to finish the process through Twitch."
        )
    }
}
