# Requirements

- java17
- gradle
- an env variable called DISCORD_TOKEN
- an env variable called TWITCH_TOKEN

# how to get discord token

todo

# how to get twitch token

open https://dev.twitch.tv/docs/irc/authenticate-bot/#getting-an-access-token
and follow the instructions under "If you just need a token for testing chat functionality"

# Build

gradle build --info

# Deploy

```shell
# local; build container
docker build -f Dockerfile -t hogwarts-bot:0.0.1 .
docker tag hogwarts-bot:0.0.1 myregistry.com/hogwarts-bot:0.0.1
docker push myregistry.com/hogwarts-bot:0.0.1

# on server; pull and start container
docker pull myregistry.com/hogwarts-bot:0.0.1
docker run -d --restart always --mount source=sqlite,target=/appl/db --env-file=hogwarts-bot.env myregistry.com/hogwarts-bot:0.0.1
# expects a hogwarts-bot.env file with the DISCORD_TOKEN variable
```
