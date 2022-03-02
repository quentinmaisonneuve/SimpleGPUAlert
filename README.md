# SimpleGPUAlert

## Introduction

The purpose of SimpleGPUAlert is to notify people when there is an Nvidia RTX 3000 series drop using several notification channels.

All notification channels you can use :

- COMMAND : Execute command in the system
- DESKTOP : Send a desktop notification
- DISCORD : Send message to discord channel
- MAIL : Send a mail throught a mail box
- TELEGRAM : Send a telegram message
- TWITTER : Send a tweet
- WEB_REQUEST : Execute a web request

## Quick links where the bot is already running
launched on 01/01/2022

- Discord Server : https://discord.gg/P9zFecmYw8
- Telegram channel : https://t.me/SimpleGPUAlert
- Twitter page  : https://twitter.com/SimpleGPUAlert

## Requirements

SimpleGPUAlert need Java 17 or above to run.

If you want to use Discord, Telegram or Twitter to send the notification, you need to provide specific token (more explanation below).

## Installation

### Manual installation

To use SimpleGPUAlert you can download the jar [here](https://github.com/quentinmaisonneuve/SimpleGPUAlert/releases) or compile it your self with this maven command :
`mvn clean compile assembly:single`

Once it's done open a command prompt and type :
`java -jar SimpleGPUAlert.jar {path to configuration.properties}`

Note : you can also start it like that `java -jar SimpleGPUAlert.jar` but you will need to have your configuration file named 'configuration.properties' in the same folder as the jar file.

### Docker

A docker repository is available [here](https://hub.docker.com/repository/docker/quentinmaisonneuve/simplegpualert)

#### Using docker run

Run this command :

`docker run -v {local_path_to_configuration.properties}:/app/simple-gpu-alert/configuration.properties 
    -v {local_path_to_log_folder}:/app/simple-gpu-alert/log 
    quentinmaisonneuve/simplegpualert:{tag}`

#### Using docker compose

Edit the [docker compose yaml file](src/main/resources/docker/docker-compose.yml) and run this command :
`docker compose up -d`

## Configuration

You can find an example (with explanation) of a configuration file [here](src/main/resources/configuration.properties)

## Bug

If you find a bug don't hesitate to post an issue, I will try to fix it as soon as possible

## Coming soon

- Remove Thread.sleep()
- Link locale / GPU with different channel configuration 
- Expose API REST with last data

## Donation

If you want you can reward my work with a donation

### [Paypal](https://www.paypal.me/quentinmaisonneuve) 

### Crypto currencies

BTC (BTC) : `3GzriVZ41snAJXK8sVERW4ww7qkgnNUkEo`

ETH (ERC20) : `0x782adfab7f63aebd82721a4be7b8afa6e18ff7b6`

CRO (CRO) : `cro1w2kvwrzp23aq54n3amwav4yy4a9ahq2kz2wtmj` / memo : `2314247699`

ADA (Cardano) : `DdzFFzCqrhsjxpXV7NW71t8p2nB7pwvkSV6uZyk8b4N7oJn5no353iFh4BgKfnXrXyzrtweZcuuae3WX9BCfnDAiCExFoDQa6Xnhot9f`

MATIC (Polygon) : `0x782adfab7f63aebd82721a4be7b8afa6e18ff7b6`

SOL (Solana) : `3Jfn6cW1pt5vj5XFdpE2C9oeyuCt15onPpNH36kYUYmH`
