/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.games;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.chalwk.Main.getBotAvatar;
import static com.chalwk.Main.getBotName;
import static com.chalwk.games.tictactoe.TicTacToe.createBoard;
import static com.chalwk.games.tictactoe.TicTacToe.startTicTacToe;
import static com.chalwk.util.util.*;

public class Game {

    public final String[] letters = { // tictactoe
            "A", "B", "C", "D",
            "E", "F", "G", "H",
            "I", "J", "K", "L",
            "M", "N", "O", "P",
            "Q", "R", "S", "T",
            "U", "V", "W", "X",
            "Y", "Z"
    };
    public final char player1 = 'X'; // tictactoe
    public final char player2 = 'O'; // tictactoe
    public final char filler = '-'; // tictactoe
    public final String challengerID;
    public final String opponentID;
    final String challengerName;
    final String opponentName;
    final String gameName;
    final int gameID;
    private final SlashCommandInteractionEvent event;
    public final Guild guild;
    public char[][] board; // tictactoe
    public char symbol; // tictactoe
    public Map<String, int[]> cell_indicators = new HashMap<>(); // tictactoe
    public String whos_turn;
    public boolean started;
    private String guildID;

    public Game(SlashCommandInteractionEvent event, OptionMapping boardSize, OptionMapping gallowsDesign, String challengerID, String opponentID, String challengerName, String opponentName, String gameName) {

        this.guild = event.getGuild();
        this.event = event;
        this.challengerID = challengerID;
        this.opponentID = opponentID;
        this.challengerName = challengerName;
        this.opponentName = opponentName;
        this.gameName = gameName;
        this.whos_turn = whoStarts();
        this.started = false;
        this.gameID = games.length;

        if (this.gameName.equals("Tic-Tac-Toe")) {
            createBoard(boardSize, this);
        }

        assert this.guild != null;
    }

    public String whoStarts() {
        Random random = new Random();
        int randomNum = random.nextInt(2);
        return (randomNum == 0) ? this.challengerName : this.opponentName;
    }

    public void setTurn() {
        this.whos_turn = (this.whos_turn.equals(this.challengerName)) ? this.opponentName : this.challengerName;
    }

    public int getGameID() {
        return this.gameID;
    }

    public EmbedBuilder getEmbed() {
        String botName = getBotName();
        String botAvatar = getBotAvatar();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(this.gameName + " Game #" + (this.gameID + 1));
        embed.addField("Challenger:", "<@" + this.challengerID + ">", true);
        embed.addField("Opponent:", "<@" + this.opponentID + ">", true);
        embed.setFooter(botName + " - Copyright (c) 2023. Jericho Crosby", botAvatar);
        embed.setColor(0x000000);
        return embed;
    }

    public void acceptInvitation(ButtonInteractionEvent event) {
        event.getMessage().delete().queue();
        if (this.gameName.equals("Tic-Tac-Toe")) {
            startTicTacToe(event, this);
        }
    }

    public void declineInvitation(ButtonInteractionEvent event, Member member) {
        privateMessage(event, member, "Your (" + this.gameName + ") invite to " + this.opponentName + " was declined.");
        event.getMessage().delete().queue();
        games = removeGame(games, this);
    }

    public void cancelInvitation(ButtonInteractionEvent event, Member member) {
        privateMessage(event, member, "Your (" + this.gameName + ") invite to " + this.opponentName + " was cancelled.");
        event.getMessage().delete().queue();
        games = removeGame(games, this);
    }
}
