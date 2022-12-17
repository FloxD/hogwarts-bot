# Requirements

- java17
- gradle
- an env variable called DISCORD_TOKEN

# Build

gradle build --info

# Deploy

```shell
# local; build container
docker build -f Dockerfile -t hogwarts-bot:0.0.1-1 .
docker tag hogwarts-bot:0.0.1-1 floxd.jfrog.io/docker-containers/hogwarts-bot:0.0.1-1
docker push floxd.jfrog.io/docker-containers/hogwarts-bot:0.0.1-1

# on server; pull and start container
docker pull floxd.jfrog.io/docker-containers/hogwarts-bot:0.0.1-1
docker run -d --restart always --mount source=sqlite,target=/appl/db --env-file=hogwarts-bot.env floxd.jfrog.io/docker-containers/hogwarts-bot:0.0.1-1
# expects a hogwarts-bot.env file with the DISCORD_TOKEN variable
```
