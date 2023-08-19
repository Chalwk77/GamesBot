/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.games.tictactoe;

import com.chalwk.games.Game;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;

import static com.chalwk.Main.getBotAvatar;
import static com.chalwk.Main.getBotName;
import static com.chalwk.games.Layout.boards;
import static com.chalwk.games.tictactoe.GameOver.gameOver;

public class TicTacToe {

    public boolean yourTurn(Game game, Member member) {
        return member.getEffectiveName().equals(game.whos_turn);
    }

    public Map<String, int[]> getCellIndicators(Game game) {
        return game.cell_indicators;
    }

    public static boolean moveAllowed(String buttonLabel, Game game) {
        int[] cells = game.cell_indicators.get(buttonLabel.toUpperCase());
        return cells != null && isCellEmpty(cells, game);
    }

    private static boolean isCellEmpty(int[] cells, Game game) {
        return game.board[cells[0]][cells[1]] == game.filler;
    }

    private static char[][] getBoard(Game game) {
        return game.board;
    }

    public static void createBoard(OptionMapping boardSize, Game game) {

        char[][] board = getBoard(game);
        String[] letters = game.letters;

        game.cell_indicators = new HashMap<>();
        game.board = boards[boardSize.getAsInt()];

        String[] alphabet = Arrays.copyOfRange(letters, 0, board.length);
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                board[row][col] = game.filler;
                game.cell_indicators.put(alphabet[row] + (col + 1), new int[]{col, row});
            }
        }
    }

    private static void setSymbol(String playerID, Game game) {
        game.symbol = (playerID.equals(game.challengerID)) ? game.player2 : game.player1;
    }

    public static void makeMove(ButtonInteractionEvent event, String buttonLabel, Game game) {

        buttonLabel = buttonLabel.toUpperCase();
        Member member = event.getMember();
        assert member != null;

        String name = member.getEffectiveName();
        setSymbol(member.getId(), game);

        int[] cells = game.cell_indicators.get(buttonLabel);
        int row = cells[0];
        int col = cells[1];

        game.setTurn();
        game.board[row][col] = game.symbol;

        EmbedBuilder embed = game.getEmbed();
        embed.addField(name + " selected " + buttonLabel, "\n\n", true);
        embed.setDescription("It is now " + game.whos_turn + "'s turn.");
        event.editMessageEmbeds(embed.build()).queue();

        gameOver(event, game);
    }

    private static EmbedBuilder getEmbedBuilder(Game game) {

        String botName = getBotName();
        String botAvatar = getBotAvatar();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("⭕.❌ Tic-Tac-Toe ❌.⭕");
        embed.setDescription("You have been invited to play TicTacToe.");
        embed.addField("Challenger:", "<@" + game.challengerID + ">", true);
        embed.addField("Opponent:", "<@" + game.opponentID + ">", true);
        embed.addField("Board: (" + game.board.length + "x" + game.board.length + ")", printBoard(game), false);
        embed.setFooter(botName + " - Copyright (c) 2023. Jericho Crosby", botAvatar);
        return embed;
    }

    public static void initTicTacToe(ButtonInteractionEvent event, Game game) {

        int boardLength = game.board.length;
        game.started = true;
        game.whos_turn = game.whoStarts();

        EmbedBuilder embed = getEmbedBuilder(game);
        embed.setDescription("The game has started. " + game.whos_turn + " goes first.");

        List<Button> buttons = new ArrayList<>();
        for (int row = 0; row < boardLength; row++) {
            for (int col = 0; col < boardLength; col++) {
                String letter = game.letters[col];
                String buttonLabel = letter + (row + 1);
                buttons.add(Button.primary(buttonLabel, buttonLabel));
            }
        }

        switch (boardLength) {
            case 3 -> {
                List<Button> row1 = buttons.subList(0, 3);
                List<Button> row2 = buttons.subList(3, 6);
                List<Button> row3 = buttons.subList(6, 9);
                event.replyEmbeds(embed.build())
                        .addActionRow(row1)
                        .addActionRow(row2)
                        .addActionRow(row3).queue();
            }
            case 4 -> {
                List<Button> row1 = buttons.subList(0, 4);
                List<Button> row2 = buttons.subList(4, 8);
                List<Button> row3 = buttons.subList(8, 12);
                List<Button> row4 = buttons.subList(12, 16);
                event.replyEmbeds(embed.build())
                        .addActionRow(row1)
                        .addActionRow(row2)
                        .addActionRow(row3)
                        .addActionRow(row4).queue();
            }
            case 5 -> {
                List<Button> row1 = buttons.subList(0, 5);
                List<Button> row2 = buttons.subList(5, 10);
                List<Button> row3 = buttons.subList(10, 15);
                List<Button> row4 = buttons.subList(15, 20);
                List<Button> row5 = buttons.subList(20, 25);
                event.replyEmbeds(embed.build())
                        .addActionRow(row1)
                        .addActionRow(row2)
                        .addActionRow(row3)
                        .addActionRow(row4)
                        .addActionRow(row5).queue();
            }
        }
    }

    private static String buildBoard(Game game) {

        StringBuilder sb = new StringBuilder();

        int len = game.board.length;
        String[] alphabet = Arrays.copyOfRange(game.letters, 0, len);

        sb.append("```");
        for (int i = 0; i < len; i++) {
            if (i == 0) {
                sb.append("    ");
                for (int j = 0; j < len; j++) {
                    sb.append(alphabet[j]).append("   ");
                }
            }
            sb.append("\n");
            sb.append(i + 1).append(" | ");
            for (int j = 0; j < len; j++) {
                sb.append(game.board[i][j]).append(" | ");
            }
            sb.append("\n");
            if (i != len - 1) {
                sb.append("  |");
                sb.append("---|".repeat(len));
            }
        }
        sb.append("```");

        return sb.toString();
    }

    private static String printBoard(Game game) {
        int len = game.board.length;
        String err = "Board size not supported: (" + len + "x" + len + ")";
        if (len < 3 || len > 5) throw new IllegalStateException(err);
        return buildBoard(game);
    }
}
