# Fake-Artist-Question-Master
This is a simple Android app that automates the Question Master role in the game [A Fake Artist Goes To New York](https://oinkgms.com/en/a-fake-artist-goes-to-new-york).
Now everyone can participate in the drawing!

## Usage ##
To use, simply enter the email addresses of all players and press Send Clue.
The app will automatically generate a random word and email it to all but one player. 
The remaining player will receive an email informing them they are the fake artist.

## Configuration ##
The app uses SendGrid to send emails, so you will have to place a valid API Key in the `local.properties` file of the project:

```
...
sendGridApiKey=YOUR_API_KEY_HERE
```

(This file is not checked in, you will have to create it manually in the root directory of the project if your IDE does not create it for you)

## TODO ##

- A settings screen to adjust the weights of the different categories used for word generation
- Include category of word in clue emails?

## Credits ##

The app uses danysantiago's [Android SDK for SendGrid](https://github.com/danysantiago/sendgrid-android)

Random word generation uses [wordgenerator.net](https://www.wordgenerator.net/pictionary-word-generator.php)
