package com.floxd.hogwartsbot.service

import com.floxd.hogwartsbot.model.TwitchMessage
import com.floxd.hogwartsbot.repository.HouseRepository
import com.floxd.hogwartsbot.toNullable
import jakarta.websocket.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URI

@ClientEndpoint
@Component
class TwitchClient(val houseRepository: HouseRepository) {

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
            sendMessage("PASS oauth:${token}")
            sendMessage("NICK wizardingworldbot")
        } catch (ex: IOException) {
            println(ex)
        }
    }

    @OnMessage
    fun processMessage(websocketMessage: String) {
        LOGGER.debug(websocketMessage.replace("\n", ""))

        if (websocketMessage.contains(":tmi.twitch.tv 376 wizardingworldbot :>")) {
            sendMessage("JOIN #elina")
        } else if (websocketMessage.startsWith(":wizardingworldbot") || websocketMessage.startsWith("PING")) {
            return
        } else {
            val parsedMessage = parseMessage(websocketMessage)

            if (parsedMessage.message.startsWith("?ping") && parsedMessage.username == "floxd") {
                sendMessage("PRIVMSG #elina :pong")
            }

            if (parsedMessage.message.startsWith("?points") && parsedMessage.username == "floxd") {
                houseRepository.findByName("h").toNullable()?.let {
                    sendMessage("PRIVMSG #elina :Hufflepuff has ${it.points} points")
                }
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

        return TwitchMessage(username, channel, message)
    }

    fun sendMessage(message: String?) {
        try {
            session!!.basicRemote.sendText(message)
        } catch (ex: IOException) {
            println(ex)
        }
    }
}
