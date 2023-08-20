/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.games.hangman;

import com.chalwk.games.Game;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.chalwk.Main.*;
import static com.chalwk.games.Layout.gallows;
import static com.chalwk.util.util.games;
import static com.chalwk.util.util.removeGame;

public class Hangman {
    static boolean yourTurn(MessageReceivedEvent event, Game game, Member member) {
        if (!member.getEffectiveName().equals(game.whos_turn)) {
            event.getMessage().delete().queue();
            return false;
        }
        return true;
    }

    static String showGuesses(List<Character> guesses) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < guesses.size(); i++) {
            sb.append(guesses.get(i).toString().toUpperCase());
            if (i != guesses.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    static String guessBox(StringBuilder word, Game game) {
        StringBuilder sb = new StringBuilder();
        sb.append("```");
        game.correct = 0;
        for (int i = 0; i < word.length(); i++) {
            char guess = word.charAt(i);
            if (game.guesses.contains(guess)) {
                game.correct++;
                sb.append("〔").append(guess).append("〕");
            } else {
                sb.append("〔 〕");
            }
        }
        sb.append("```");
        return sb.toString();
    }

    static boolean getGuess(String character, StringBuilder word, Game game) {
        char guess = character.charAt(0);
        game.guesses.add(guess);
        return word.toString().contains(character);
    }

    static void setStage(int stage, Game game) {
        if (stage < 0) stage = 0;
        game.stage = game.layout[stage];
    }

    public static String getEmbedID(Game game) {
        return game.embedID;
    }

    private static void setEmbedID(String embedID, Game game) {
        game.embedID = embedID;
    }

    private static void setMessageID(ButtonInteractionEvent event, Game game) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setEmbedID(event.getChannel().getLatestMessageId(), game);
            }
        }, 500);
    }

    public static void setLayout(int layoutIndex, Game game) {
        if (layoutIndex < 0) layoutIndex = 0;
        game.layout = gallows[layoutIndex];
    }

    static EmbedBuilder getEmbed(Game game) {

        String botName = getBotName();
        String botAvatar = getBotAvatar();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("\uD83D\uDD74 \uD80C\uDF6F Hangman \uD80C\uDF6F \uD83D\uDD74");
        embed.addField("Challenger:", "<@" + game.challengerID + ">", true);
        embed.addField("Opponent:", "<@" + game.opponentID + ">", true);
        embed.addField("Hangman:", printHangman(game), false);
        embed.setFooter(botName + " - Copyright (c) 2023. Jericho Crosby", botAvatar);
        embed.setColor(0x000000);
        return embed;
    }

    public static void startHangman(ButtonInteractionEvent event, Game game) {
        newRandomWord(game);

        game.started = true;
        game.state = game.layout.length - 1;
        game.whos_turn = game.whoStarts();

        setStage(game.state, game);
        EmbedBuilder embed = getEmbed(game);
        embed.setDescription("The game has started. " + game.whos_turn + " goes first.");
        embed.addField("Guess a letter or the word: " + game.word.length() + " characters", "", false);
        embed.addField("Characters:", "```" + "〔 〕".repeat(game.word.length()) + "```", false);
        event.replyEmbeds(embed.build()).queue();
        setMessageID(event, game);
    }

    private static void newRandomWord(Game game) {
        game.word = words.getString(new Random().nextInt(words.length()));
    }

    static void updateEmbed(StringBuilder word, Game game, MessageReceivedEvent event, int color) {

        game.setTurn();
        event.getMessage().delete().queue();
        String guess_box = guessBox(word, game);

        EmbedBuilder embed = getEmbed(game);
        if (GameOver.gameOver(word, game, event, embed)) {
            games = removeGame(games, game);
            return;
        }

        embed.setDescription("It's now " + game.whos_turn + "'s turn.");
        embed.addField("Characters:", guess_box, false);
        embed.addField("Guesses: " + showGuesses(game.guesses), " ", false);
        embed.setColor(color);
        editEmbed(game, event, embed);
    }

    static void editEmbed(Game game, MessageReceivedEvent event, EmbedBuilder embed) {
        event.getChannel()
                .retrieveMessageById(getEmbedID(game))
                .queue(message -> message.editMessageEmbeds(embed.build()).queue());
    }

    static String printHangman(Game game) {
        return "```" + game.stage + "```";
    }
}
