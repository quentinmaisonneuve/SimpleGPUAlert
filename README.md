# SimpleGPUAlert

## Introduction

The purpose of SimpleGPUAlert is to notification people when there is a Nvidia RTX 3000 series drop using several notification channel such as Telegram or Discord.

### Requirements

SimpleGPUAlert need Java 17 or above to run.
If you want to use Discord, Telegram or Twitter to send the notification, you need to provide specific token (more explanation below).

## Installation

To use SimpleGPUAlert you can download the jar [here]() or compile it your self with this maven commande :
`mvn clean compile assembly:single`

Once it's done open a command prompt and type :
`java -jar SimpleGPUAlert.jar {path to configuration.properties}`

Note : you can also start it like that `java -jar SimpleGPUAlert.jar` but you will need to have a file named 'configuration.properties' in the same folder as the jar file.

## Configuration

### Global properties

`NOTIFICATION_CHANNEL`

Channels of notification separate by a comma (available channels : DESKTOP, DISCORD, MAIL, TELEGRAM, TWITTER, WEB_REQUEST | more to come)
If there is more than one channel for notification, notification are send by order on the list

`LOCALE`

Known locale that's actually working : DE, ES, FR, IT, NL, UK
You can check if a locale is available with the link below and by replacing the %s by your locale :
Example for France : https://api.store.nvidia.com/partner/v1/feinventory?skus=FR&locale=FR

`NVIDIA_API_LINK`

https://api.store.nvidia.com/partner/v1/feinventory?skus=%s&locale=%s

`GPU`

GPU you be to be alerted on, actually available : 3060ti, 3070, 3070ti, 3080, 3080ti, 3090

`REFRESH_INTERVAL`

Time (in millisecond) between each request to Nvidia API (all request on 'api.store.nvidia.com' are made in the same thread)

`TIMEOUT_NOTIFICATION_CHANNEL`

Time (in millisecond) between each notification (by channel)


`TIMEOUT_NOTIFICATION_DROP`

Time (in minute) between each notification (by locale by GPU)
If a drop (for a specific channel, locale and GPU) is still available after x minute will send a new notification (0 to send only one notification per drop)
Will not work for Twitter because of API's limiation.

`LOG_LEVEL`

If blank default is INFO, DEBUG if you want to send me the log if you encounter a bug, OFF to disable logs

`TEST_NOTIFICATION`

If you want to test your parameters you can use this property :
If true will send a notification anyway even if the GPU is not available or the timeout is not reached

### Text template
- `%i` : will be replaced by the ID of the GPU (NVGFT070 = 3070, NVGFT070T = 3070ti, ...)
- `%g` : will be replaced by the GPU name
- `%p` : will be replaced by the purchase link
- `%l` : will be replaced by locale
- `%b` : will be replaced by the price

## Desktop properties

If you click on the desktop notification it will open the purchase link in your browser

`DESKTOP_TITLE_TEMPLATE`

Title template of the desktop notification

`DESKTOP_MESSAGE_TEMPLATE`

Message template of the desktop notification


## Discord properties

`DISCORD_MESSAGE_API_ENDPOINT`

https://discord.com/api/v9/channels/%s/messages

`DISCORD_BOT_TOKEN`

It is required to be able to use discord notification

`CHANNEL_ID`

Can be found in url when discord is used in brower when you are in a channel : https://discord.com/channels/{guildId}/{channelId}

`DISCORD_TITLE_TEMPLATE`

Message title (the link is automatically put in the title)

`DISCORD_MESSAGE_TEMPLATE`

Message body


## Mail properties

You may need to setup your mail box to ensure it can be used by a third party application.
Example for Gmail :
- Two Steps Verification should be turned off.
- Allow Less Secure App(should be turned on) : https://myaccount.google.com/lesssecureapps
- Try this link if it still doesn't work : https://accounts.google.com/DisplayUnlockCaptcha

`USER_MAIL`
Your email adress

`USER_PASSWORD`

Your password

`USER_MAIL_RECIPIENTS`

If blank send the mail to USER_MAIL

`SMTP_SERVER_ADRESS`
`PORT`

Mailing server parameters

`MAIL_SUBJECT_TEMPLATE`

Subject template of the email
 
`MAIL_MESSAGE_TEMPLATE`

Message template of the email


## Telegram properties
TELEGRAM_API_LINK = https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s
API_TOKEN =
CHAT_ID =


`TELEGRAM_MESSAGE_TEMPLATE`

Message template

%r : will de replaced by a line return


## Twitter properties

Note : You need to have Elevated access enable on your Projects : you can apply a request for it here : https://developer.twitter.com/en/portal/products/elevated


`CONSUMER_KEY`
`CONSUMER_SECRET`
`ACCESS_TOKEN`
`ACCESS_TOKEN_SECRET`

API tokens

`TWEET_TEMPLATE`

Message template

\r\n : line return in your tweet


## Web request properties

`URL_TO_CALL`

Url will be called when a card is up, response body and status code will be logged

`REQUEST_TYPE`

You can choose the type of http request (GET, PUT or POST) if null or empty GET if the default

`REQUEST_BODY`

You can use message template on this field (%i, %g, %l, ... see above)
