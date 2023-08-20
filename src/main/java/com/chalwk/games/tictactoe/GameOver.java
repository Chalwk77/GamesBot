/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.games.tictactoe;

import com.chalwk.games.Game;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import static com.chalwk.games.Layout.WINNING_COMBINATIONS;
import static com.chalwk.games.tictactoe.TicTacToe.getEmbedBuilder;
import static com.chalwk.util.util.games;
import static com.chalwk.util.util.removeGame;

public class GameOver {
    private static void endGame(Game game, int state, ButtonInteractionEvent event) {

        Member member = event.getMember();
        assert member != null;

        EmbedBuilder embed = getEmbedBuilder(game);
        if (state == 0) {
            embed.setDescription("Game Over! It's a draw!");
        } else {
            String name = member.getEffectiveName();
            embed.setDescription("Game Over! " + name + " has won the game!");
        }
        event.getHook().deleteOriginal().queue();
        event.getHook().sendMessageEmbeds(embed.build()).queue();

        games = removeGame(games, game);
    }

    public static boolean gameOver(ButtonInteractionEvent event, Game game) {

        char[][] board = game.board;

        int count = 0;
        for (char[] chars : board) {
            for (char aChar : chars) {
                if (aChar != game.filler) {
                    count++;
                }
            }
        }

        if (count == board.length * board.length) {
            endGame(game, 0, event);
            return true;
        } else if (checkWinner(game)) {
            endGame(game, 1, event);
            return true;
        }

        return false;
    }

    private static boolean checkWinner(Game game) {
        char[][] board = game.board;
        int len = board.length;
        switch (len) {
            case 3 -> {
                return board1(board, game);
            }
            case 4 -> {
                return board2(board, game);
            }
            case 5 -> {
                return board3(board, game);
            }
        }

        return false;
    }

    private static boolean board1(char[][] board, Game game) {
        for (int[] combination : WINNING_COMBINATIONS[0]) {
            int x = combination[0];
            int y = combination[1];
            int z = combination[2];
            if (board[x / 3][x % 3] == game.symbol
                    && board[y / 3][y % 3] == game.symbol
                    && board[z / 3][z % 3] == game.symbol) {
                return true;
            }
        }
        return false;
    }

    private static boolean board2(char[][] board, Game game) {
        for (int[] combination : WINNING_COMBINATIONS[1]) {
            int x = combination[0];
            int y = combination[1];
            int z = combination[2];
            int w = combination[3];
            if (board[x / 4][x % 4] == game.symbol
                    && board[y / 4][y % 4] == game.symbol
                    && board[z / 4][z % 4] == game.symbol
                    && board[w / 4][w % 4] == game.symbol) {
                return true;
            }
        }
        return false;
    }

    private static boolean board3(char[][] board, Game game) {
        for (int[] combination : WINNING_COMBINATIONS[2]) {
            int x = combination[0];
            int y = combination[1];
            int z = combination[2];
            int w = combination[3];
            int v = combination[4];
            if (board[x / 5][x % 5] == game.symbol
                    && board[y / 5][y % 5] == game.symbol
                    && board[z / 5][z % 5] == game.symbol
                    && board[w / 5][w % 5] == game.symbol
                    && board[v / 5][v % 5] == game.symbol) {
                return true;
            }
        }
        return false;
    }
}
