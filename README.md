# GamesBot

A Discord Bot written in Java using the [JDA](https://github.com/discord-jda/JDA) library.

GamesBot aims to bring a variety of classic pen & paper games to Discord, starting with Hangman and Tic Tac Toe. More to
come in the future!

## Games:

- Hangman (2 player hangman)
- Tic Tac Toe (2 player hangman)

Commands:

- `/setup` `game` `channel id`
  - Use this command to set up a game in a channel.

- `/hangman` `opponent` `gallows design`
  - Use this command to start a game of hangman.

- `/tictactoe` `opponent` `board size`
  - Use this command to start a game of Tic Tac Toe.

Bot Token:
Create a file called *auth.token* in the root directory and put your bot token in it.