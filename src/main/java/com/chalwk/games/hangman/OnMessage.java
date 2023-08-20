/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.games.hangman;

import com.chalwk.games.Game;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.chalwk.util.util.games;

public class OnMessage {

    public static void onMessage(MessageReceivedEvent event) {

        Member member = event.getMember();
        String memberID = member.getId();

        for (Game game : games) {

            String challengerID = game.challengerID;
            String opponentID = game.opponentID;

            if (memberID.equals(challengerID) || memberID.equals(opponentID) && game.started) {
                if (!Hangman.yourTurn(event, game, member)) return;

                int color = 0x00ff00; // green
                String input = event.getMessage().getContentRaw();
                String word = game.word;
                if (input.length() > 1) {
                    if (input.contentEquals(word)) { // guessed the whole word
                        game.guessed_whole_word = true;
                    } else {
                        game.state--;
                    }
                } else if (!Hangman.getGuess(input, new StringBuilder(word), game)) {
                    game.state--;
                    color = 0xff0000; // red
                }
                Hangman.setStage(game.state, game);
                Hangman.updateEmbed(new StringBuilder(word), game, event, color);
            }
        }
    }

}
