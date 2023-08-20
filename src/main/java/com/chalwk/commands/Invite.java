/* Copyright (c) 2023, GamesBot. Jericho Crosby <jericho.crosby227@gmail.com> */
package com.chalwk.commands;

import com.chalwk.games.Game;
import com.chalwk.listeners.CommandInterface;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.chalwk.Main.hangmanConfig;
import static com.chalwk.Main.ticTacToeConfig;
import static com.chalwk.games.Layout.boards;
import static com.chalwk.games.Layout.gallows;
import static com.chalwk.games.hangman.Hangman.hangmanSubmission;
import static com.chalwk.games.tictactoe.TicTacToe.tictactoeSubmission;
import static com.chalwk.util.util.addGame;
import static com.chalwk.util.util.games;

public class Invite extends Throwable implements CommandInterface {

    private String challengerID;
    private String opponentID;
    private OptionMapping board_size;
    private OptionMapping gallows_design;
    private String gameName;
    private String challengerName;
    private String opponentName;

    private static boolean allowInvite(SlashCommandInteractionEvent event, String gameName) {

        String guildID = event.getGuild().getId();
        String channelID = event.getChannel().getId();

        JSONObject settings = (gameName.equals("Tic-Tac-Toe")) ? ticTacToeConfig : hangmanConfig;
        JSONArray config = getConfig(event, gameName, settings, guildID);
        if (config == null) return false;

        String configChannelID = config.get(0).toString();
        if (!configChannelID.equals(channelID)) {
            event.reply("You cannot play (" + gameName + ") in this channel.").setEphemeral(true).queue();
            return false;
        }

        return true;
    }

    @Nullable
    private static JSONArray getConfig(SlashCommandInteractionEvent event, String gameName, JSONObject settings, String guildID) {
        JSONArray config;
        try {
            config = settings.getJSONArray(guildID);
        } catch (Exception e) {
            event.reply(gameName + " is not setup for this Server. Use `/setup` to do this.").setEphemeral(true).queue();
            return null;
        }
        return config;
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Invite someone to play a game with you.";
    }

    @Override
    public List<OptionData> getOptions() {

        List<OptionData> options = new ArrayList<>();
        optionData result = getOptionData();

        options.add(result.games());
        options.add(result.opponent());
        options.add(result.ticTacToeBoard());
        options.add(result.gallows_design());
        return options;
    }

    @NotNull

    private optionData getOptionData() {

        OptionData games = new OptionData(OptionType.STRING, "game", "The game you want to play.");
        OptionData opponent = new OptionData(OptionType.USER, "opponent", "The user you want to invite.");
        OptionData ticTacToeBoard = new OptionData(OptionType.INTEGER, "board", "The board size you want to play on.");
        OptionData gallows_design = new OptionData(OptionType.INTEGER, "gallows_design", "The Gallows design you want to use.");

        games.addChoice("Tic-Tac-Toe", "Tic-Tac-Toe");
        games.addChoice("Hangman", "Hangman");

        games.setRequired(true);
        opponent.setRequired(true);

        for (int i = 0; i < boards.length; i++) {
            String size = boards[i].length + "x" + boards[i].length;
            ticTacToeBoard.addChoice(size, i);
        }

        for (int i = 0; i < gallows.length; i++) {
            gallows_design.addChoice("Gallows: " + (i + 1), i);
        }

        return new optionData(games, opponent, ticTacToeBoard, gallows_design);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        OptionMapping game = event.getOption("game");
        OptionMapping opponent = event.getOption("opponent");
        OptionMapping board_size = event.getOption("board"); // tic-tac-toe
        OptionMapping gallows_design = event.getOption("gallows_design"); // hangman

        Member member = event.getMember();

        assert opponent != null;
        assert game != null;
        assert member != null;

        setOptionData(board_size, gallows_design, opponent, game, member);

        if (!allowInvite(event, gameName)) return;
        if (opponent.getAsUser().isBot()) {
            event.reply("You cannot invite a bot to play " + gameName + ".").setEphemeral(true).queue();
        } else if (challengerID.equals(opponentID)) {
            event.reply("You cannot invite yourself to play " + gameName + ".").setEphemeral(true).queue();
        } else {
            invitePlayer(event);
        }
    }

    private void setOptionData(OptionMapping board_size, OptionMapping gallows_design, OptionMapping opponent, OptionMapping game, Member member) {
        this.board_size = board_size;
        this.gallows_design = gallows_design;
        this.challengerID = member.getId();
        this.opponentID = opponent.getAsUser().getId();
        this.challengerName = member.getEffectiveName();
        this.opponentName = opponent.getAsUser().getEffectiveName();
        this.gameName = game.getAsString();
    }

    private void invitePlayer(SlashCommandInteractionEvent event) {
        Game game = new Game(event, board_size, gallows_design, challengerID, opponentID, challengerName, opponentName, gameName);
        if (gameName.equals("Tic-Tac-Toe")) {
            tictactoeSubmission(event, game);
        } else if (gameName.equals("Hangman")) {
            hangmanSubmission(event, game);
        }
        games = addGame(games, game);
    }

    private record optionData(OptionData games, OptionData opponent, OptionData ticTacToeBoard,
                              OptionData gallows_design) {

    }
}
