package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.exception.BotException
import com.floxd.hogwartsbot.model.TwitchMessage
import com.floxd.hogwartsbot.service.commands.Command
import jakarta.websocket.*
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URI
import java.time.LocalDateTime

@ClientEndpoint
@Component
class TwitchClient(val commands: List<Command>) {

    private val LOGGER = LoggerFactory.getLogger(DiscordCommandListener::class.java)

    var session: Session? = null
    final val token: String

    init {
        token = System.getenv("TWITCH_TOKEN")
        assert(token != null, { "env var TWITCH_TOKEN must be set" })

        try {
            val container = ContainerProvider.getWebSocketContainer()
            container.connectToServer(this, URI("wss://irc-ws.chat.twitch.tv:443"))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @OnOpen
    fun onOpen(session: Session) {
        this.session = session
        try {
            sendWebsocketMessage("PASS oauth:${token}")
            sendWebsocketMessage("NICK wizardingworldbot")
        } catch (ex: IOException) {
            println(ex)
        }
    }

    @OnMessage
    fun processMessage(websocketMessage: String) {
        LOGGER.debug(websocketMessage.replace("\n", ""))

        if (websocketMessage.contains(":tmi.twitch.tv NOTICE * :Login authentication failed")) {
            LOGGER.error("Twitch Authentication Failed")
            session!!.close()
            return
        } else if (websocketMessage.contains(":tmi.twitch.tv 376 wizardingworldbot :>")) {
            sendWebsocketMessage("JOIN #elina")
        } else if (websocketMessage.startsWith("PING")) {
            sendWebsocketMessage("PONG :tmi.twitch.tv")
        } else if (websocketMessage.startsWith(":wizardingworldbot")) {
            return
        } else {
            val parsedMessage = parseMessage(websocketMessage)

            if (!parsedMessage.message.startsWith("?")) {
                // return early if chat message isn't a command
                return
            }

            val channel = parsedMessage.channel
            val command = parsedMessage.message.split(" ")

            if (command.size < 1) {
                return
            }

            try {
                val commandName = command[0].replace("?", "")
                commands.find { it.commandName() == commandName }?.let {
                    sendChatMessage(channel, it.runTwitch(parsedMessage))
                }
            } catch (e: BotException) {
                sendChatMessage(channel, e.message)
            } catch (e: UnsupportedOperationException) {
                sendChatMessage(channel, "Not implemented yet")
            } catch (e: Exception) {
                sendChatMessage(channel, "Unknown error happened. Message FloxD if you need help.")
                LOGGER.error("Unknown error happened.", e)
            }
        }
    }

    /**
     * twitch message format e.g.:
     * :zebrahestur!zebrahestur@zebrahestur.tmi.twitch.tv PRIVMSG #elina :I regret not gifting 100 subs everyday to elina
     *
     * TODO clean up ugly chat message parsing
     */
    fun parseMessage(websocketMessage: String): TwitchMessage {
        val username = websocketMessage.substring(1, websocketMessage.indexOf("!"))
        val temp = websocketMessage.substring(websocketMessage.indexOf("#"))
        val channel = temp.substring(1, temp.indexOf(" "))
        val message = websocketMessage.substring(websocketMessage.indexOf(":", 1) + 1)
                .replace("\r\n", "")

        return TwitchMessage(username, channel, message)
    }

    fun sendChatMessage(channel: String, message: String) {
        sendWebsocketMessage("PRIVMSG #${channel} :${message}")
    }

    fun sendWebsocketMessage(message: String) {
        try {
            if (session!!.isOpen) {
                session!!.basicRemote.sendText(message)
            } else {
                LOGGER.debug("Session is closed, skipping sending websocket message \"${message}\"")
            }
        } catch (ex: IOException) {
            println(ex)
        }
    }

    /**
     * hacky disconnect fix
     * the bot keeps disconnecting after a while even though there's a handler for the PING command
     * but sending periodic messages is a hacky workaround to fix this for now
     * the ping is every 5 minutes and we send a chat message every minute
     *
     * initialDelay = 30 seconds
     * fixedRate = 60 seconds
     */
    @Scheduled(initialDelay = 30000, fixedRate = 60000)
    fun ping() {
        LOGGER.debug("Sending scheduled keepalive message")
        sendChatMessage("wizardingworldbot", LocalDateTime.now().toString())
    }
}
