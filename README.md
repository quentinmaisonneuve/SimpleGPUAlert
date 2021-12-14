# SimpleGPUAlert

## Introduction

The purpose of SimpleGPUAlert is to notification people when there is a Nvidia RTX 3000 series drop using several notification channel such as Telegram or Discord.

## Quick links where the bot is already running
launched the 09/12/2021

- Twitter page  : https://twitter.com/SimpleGPUAlert
- Telegram channel : https://t.me/SimpleGPUAlert

## Requirements

SimpleGPUAlert need Java 17 or above to run.

If you want to use Discord, Telegram or Twitter to send the notification, you need to provide specific token (more explanation below).

## Installation

### Manual installation

To use SimpleGPUAlert you can download the jar [here]() or compile it your self with this maven commande :
`mvn clean compile assembly:single`

Once it's done open a command prompt and type :
`java -jar SimpleGPUAlert.jar {path to configuration.properties}`

Note : you can also start it like that `java -jar SimpleGPUAlert.jar` but you will need to have your configuration file named 'configuration.properties' in the same folder as the jar file.

### Docker

Work in progress...

## Configuration

You can find an example (with explanation) of a configuration file [here](src/main/resources/configuration.properties)

## Bug

If you find a bug don't hesitate to post an issue, I will try to fix it as soon as possible

## TODO

Before public repo : docker

Adding header for WEB_REQUEST notification

Make EXECUTE_COMMAND, INSTAGRAM, FACEBOOK, MESSENGER, WHATSAPP notifications

Expose API REST with last data

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
