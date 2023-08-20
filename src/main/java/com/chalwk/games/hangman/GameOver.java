/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.games.hangman;

import com.chalwk.games.Game;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GameOver {
    static boolean gameOver(StringBuilder word, Game game, MessageReceivedEvent event, EmbedBuilder embed) {
        if (word.length() == game.correct || game.guessed_whole_word) {
            embed.addField("\uD83D\uDFE2 GAME OVER. The word was (" + word + "). " + game.whos_turn + " wins!", " ", false);
            embed.setColor(0x00ff00);
            Hangman.editEmbed(game, event, embed);
            return true;
        } else if (game.state == 0) {
            embed.addField("\uD83D\uDD34 GAME OVER. The word was (" + word + "). The man was hung!", " ", false);
            embed.setColor(0xff0000);
            Hangman.editEmbed(game, event, embed);
            return true;
        }
        return false;
    }
}
