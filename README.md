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
docker build -f Dockerfile -t hogwarts-bot:latest .
docker tag hogwarts-bot:latest myregistry.com/hogwarts-bot:latest
docker push myregistry.com/hogwarts-bot:latest

# on server; pull and start container
docker ps | grep hogwarts-bot | awk '{print $1}' | xargs docker stop || true
docker pull myregistry.com/hogwarts-bot:latest
docker run -d --restart always --mount source=sqlite,target=/appl/db --env-file=hogwarts-bot.env myregistry.com/hogwarts-bot:latest
docker ps | grep hogwarts-bot | awk '{print $1}' | xargs docker logs -f || true
# expects a hogwarts-bot.env file with the DISCORD_TOKEN variable
```
