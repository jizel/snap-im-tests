# Integration Message
The integration message contains integrationd ID that represent single integration run.

# Trigger Integration Starter
Send starting message _integration-starter-message.json_ to any _integration.*.start_ activemq queue.

The starting message contains timestamp. For example timestamp _{"fireTime":"2017-04-21T01:00:00Z"}_ triggers
integration start to create gathering messages for properties in timezones matching UTC-1.

# Trigger DataGathering
Send gathering message _\<integration\>-gather-message.json_ to _integration.\<integration\>.gather_ activemq queue.

## Message Group ID
The Message Group ID is used to avoid rate limits. Fill Message Group ID before sending gathering message:
* instagram - message group ID is integration access token
* twitter - message group ID is integration access token
